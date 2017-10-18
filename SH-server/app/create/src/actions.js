export const changeState = (state) => ({
    type: 'CHANGE_STATE',
    state,
});

export const createGame = (gameInfo) => ({
    type: 'CREATE_GAME',
    gameInfo,
});

export const updateStats = (stats) => ({
	type: 'UPDATE_STATS',
	stats,
});
