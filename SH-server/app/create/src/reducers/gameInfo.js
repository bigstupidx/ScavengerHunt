import * as constants from '../constants';

const initialState = {
    appState: constants.ONBOARD_STATE,
    stats: {
        player_stats: [],
        objective_stats: [],
    },
}

const gameInfo = (state = initialState, action) => {
    switch (action.type) {
    case 'CHANGE_STATE':
        return {
            ...state,
            appState: action.state,
        };
        
    case 'CREATE_GAME':
        return {
            ...state,
            ...action.gameInfo,
        };

    case 'UPDATE_STATS':
        return {
            ...state,
            stats: action.stats,
        };

    default:
        return state;
    }
}

export default gameInfo
