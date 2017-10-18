import React from 'react';
import { connect } from 'react-redux';

import { post } from '../http';
import Track from '../components/Track';
import { updateStats } from '../actions';
import { TRACK_DATA_ENDPOINT, POLLING_RATE } from '../constants';

class TrackData extends React.Component {
	constructor(props) {
		super(props)
		this.state = { statsIntervalId: null, data: {} }
	}

	pollStats() {
		post.call(this, TRACK_DATA_ENDPOINT, {
			game_id: this.props.game_key
		}, (responseJson) => {
			// TODO: handle non 200 status
			console.log('JSON received:', responseJson);
			this.props.updateStats(responseJson.data);
			this.createData();
		});
	}

	createData() {
		console.log("CREATING DATA");
		let data = {player: [], objectives: []};

		for (const objective of this.props.objectives) {
			data.objectives.push(objective.name);
		}

		for (const player of this.props.stats) {
			let objective_times = [];
			for (const objective in this.props.objectives) {
				if (objective.id in player.objectives_complete)
					objective_times.push(player.objective_times[objective.id]);
				else
					objective_times.push(-1);
			}
			data.player.push([player.name, objective_times]);
		}
		console.log(data);
		this.setState({ data });
	}

	componentWillMount() {
		this.pollStats();
		this.setState({
			statsIntervalId: setInterval(this.pollStats, POLLING_RATE)
		});
	}

	componentWillUnmount() {
		clearInterval(this.state.statsIntervalId);
	}

	render() {
		return (
		<div className='trackData'>
			<Track data={this.state.data} />
		</div>);
	}
}

const mapStateToProps = (state) => {
	const { key } = state.gameInfo;
	const { player_stats, objective_stats } = state.gameInfo.stats;

	let objectives = [];
	for (const objective of objective_stats) {
		objectives.push({
			id: objective.id,
			name: objective.name,
		});
	}

	console.log(objective_stats);

	return {
		game_key: key,
		objectives,
		stats: player_stats,
	};
};

const mapDispatchToProps = (dispatch) => {
	return {
		updateStats: (stats) => dispatch(updateStats(stats))
	};
};

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(TrackData);