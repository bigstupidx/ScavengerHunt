import React, { Component} from 'react';
import './AddObjectiveDialog.css';

class AddObjectiveDialog extends Component {

	constructor(props){
		super(props);
		this.state={
			name:'',
			description: '',
		}

		this.handleNameChange = this.handleNameChange.bind(this);
		this.handleDescriptionChange = this.handleDescriptionChange.bind(this);
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	handleNameChange(event){
		this.setState({
			name: event.target.value,
			description: this.state.description,
		});
	}

	handleDescriptionChange(event){
		this.setState({
			name: this.state.name,
			description: event.target.value,
		});
	}

	handleSubmit(event){
		const objective = this.state;
		this.props.submit(objective);

		event.preventDefault();
	}

	render(){
		console.log("add objective");
		return(
			<div className='AddObjectiveDialog'>
				<div className='close' onClick={this.props.close}>
					x
				</div>
				<div className='innerObjectiveDialog'>
					<h2>Add an objective!</h2>

					Selected Lat: {this.props.lat} <br></br>

					Selected Lng: {this.props.lng} <br></br><br></br>

					<form onSubmit={this.handleSubmit}>
						<label>
							Objective Name: &nbsp;&nbsp;
							<input type='text' value={this.state.name} onChange={this.handleNameChange}/>
							<br></br>
						</label>
						<label>
							Objective Description: &nbsp;&nbsp;
							<input type='text' value={this.state.description} onChange={this.handleDescriptionChange}/>
							<br></br>
							<br></br>
						</label>

						<input type="submit" value="Submit" />
					</form>
				</div>
			</div>
		)
	}
}

export default AddObjectiveDialog;