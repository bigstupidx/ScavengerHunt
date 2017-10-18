import React from 'react';
import { connect } from 'react-redux';

import TrackForm from '../components/TrackForm';
import { createGame, changeState } from '../actions';
import { TRACK_STATE } from '../constants';

class TrackFormPage extends React.Component {
    render() {
        return (
            <div className='trackForm'>
                <TrackForm onSubmit={ this.props.onSubmit } />
            </div>
        )
    }
}

const mapDispatchToProps = (dispatch) => {
    return {
        onSubmit: (values) => {
            const { key } = values;

            // TODO: check if game key is legit...
            dispatch(createGame({ key }));
            dispatch(changeState(TRACK_STATE));
        }
    };
};

export default connect(undefined, mapDispatchToProps)(TrackFormPage)
