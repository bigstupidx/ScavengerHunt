import React from 'react';
import { connect } from 'react-redux';

import * as constants from '../constants';

import OnBoard from './OnBoard';
import GameFormPage from './GameFormPage';
import TrackPage from './TrackPage';
import ObjectiveSetup from './ObjectiveSetup';
import TrackFormPage from './TrackFormPage';
import TrackData from './TrackData';

const App = ({ state }) => (
    <div className='App'>
        {state === constants.ONBOARD_STATE && <OnBoard />}
        {state === constants.CREATE_STATE && <GameFormPage />}
        {state === constants.TRACK_STATE && <TrackPage />}
        {state === constants.OBJECTIVE_SETUP_STATE && <ObjectiveSetup />}
        {state === constants.TRACK_FORM_STATE && <TrackFormPage />}
        {state === constants.TRACK_STATE && <TrackData />}
    </div>
)

const mapStateToProps = (state) => {
    return {
        state: state.gameInfo.appState,
    };
};

export default connect(mapStateToProps)(App);
