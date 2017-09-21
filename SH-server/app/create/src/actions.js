export const changeState = (state) => ({
    type: 'CHANGE_STATE',
    state,
});

export const createGame = (gameInfo) => ({
    type: 'CREATE_GAME',
    gameInfo,
});

export const updateStats = (gameStats) => ({
    type: 'UPDATE_STATS',
    gameStats: {
        playerStats: gameStats.player_stats,
        objectiveStats: gameStats.objective_stats,
    },
});
