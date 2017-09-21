import React from 'react';
import { connect } from 'react-redux';

import { post } from '../http';
import { updateStats } from '../actions';
import { GAME_STATS_ENDPOINT } from '../constants';

import Track from '../components/Track';

const POLL_RATE = 5000 // seconds

class TrackPage extends React.Component {

    pollStats() {
        post(GAME_STATS_ENDPOINT, {
            game_id: 'UIUS',//this.props.gameKey,
        }, (responseJson) => {
            console.log('GAME STATS RESPONSE', responseJson);
            console.log(this.props);
            this.props.updateStats(responseJson);
        });
    }

    componentWillMount() {
        this.pollStats()
        const statsPollId = setInterval(this.pollStats.bind(this), POLL_RATE);
        this.setState({ statsPollId });
    }

    componentWillUnmount() {
        clearInterval(this.state.statsPollId);
    }

    render() {
        return (
            <div className='track'>
                <Track data={this.props.stats.playerStats} />
            </div>
        );
    }
}

const mapStateToProps = (state) => {
    const { key, playerStats, objectiveStats } = state;
    return { 
        gameKey: key,
        stats: {
            playerStats,
            objectiveStats,
        }
    };
};

const mapDispatchToProps = (dispatch) => {
    return {
        updateStats: (stats) => {
            dispatch(updateStats(stats));
        },
    };
};

export default connect(
    mapStateToProps,
    mapDispatchToProps
)(TrackPage);
