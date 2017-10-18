import React from 'react';
import { connect } from 'react-redux';
import { changeState } from '../actions';
import { CREATE_STATE, TRACK_FORM_STATE } from '../constants';

import './OnBoard.css';
import logo from '../logo.png'

const OnBoard = ({ onTrackClick, onCreateClick }) => {
    return (
        <div className='onBoard'>
            <div className='mapBg' />
            <div className='logo'>
                <img src={logo} alt='map-bg' />
            </div>
            <button className='button -regular -green' id='createBtn' onClick={onCreateClick}>
                Create
            </button>
            <button className='button -regular -blue' id='trackBtn' onClick={onTrackClick}>
                Track
            </button>
        </div>
    );
}

const mapDispatchToProps = (dispatch) => {
    return {
        onCreateClick: () => {
            dispatch(changeState(CREATE_STATE));
        },
        onTrackClick: () => {
            dispatch(changeState(TRACK_FORM_STATE));
        }
    }
}


export default connect(undefined, mapDispatchToProps)(OnBoard);
