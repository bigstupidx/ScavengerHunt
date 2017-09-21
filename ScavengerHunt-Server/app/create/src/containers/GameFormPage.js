import React from 'react';
import { connect } from 'react-redux';

import { post } from '../http';
import { createGame, changeState } from '../actions';
import { CREATE_ENDPOINT, OBJECTIVE_SETUP_STATE } from '../constants';

import GameForm from '../components/GameForm';

class GameFormPage extends React.Component {
    render() {
        return (
            <div className='gameForm'>
                <GameForm onSubmit={ this.props.onSubmit } />
            </div>
        )
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        onSubmit: (values) => {
            const { name, description } = values;
            // create game on server
            post(CREATE_ENDPOINT, {
                name,
                description,
            }, (responseJson) => {
                // TODO: handle non 200 status
                console.log('CREATE RESPONSE:', responseJson);
                dispatch(createGame({
                    name,
                    description,
                    key: responseJson.game_key
                }))
                dispatch(changeState(OBJECTIVE_SETUP_STATE));
            });
        }
    };
};

export default connect(
    undefined,
    mapDispatchToProps
)(GameFormPage);
