import React from 'react';
import { connect } from 'react-redux';

import * as constants from '../constants';

import OnBoard from './OnBoard';
import TrackPage from './TrackPage';
import GameFormPage from './GameFormPage';
import ObjectiveSetup from './ObjectiveSetup';

const App = ({ state }) => (
    <div className='App'>
        {state === constants.ONBOARD_STATE && <OnBoard />}
        {state === constants.CREATE_STATE && <GameFormPage />}
        {state === constants.TRACK_STATE && <TrackPage />}
        {state === constants.OBJECTIVE_SETUP_STATE && <ObjectiveSetup />}
    </div>
)

const mapStateToProps = (state) => {
    return {
        state: state.gameInfo.appState,
    };
};

export default connect(mapStateToProps)(App);
