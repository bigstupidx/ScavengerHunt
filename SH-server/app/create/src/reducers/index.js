import { combineReducers } from 'redux'
import { reducer as form } from 'redux-form'

import gameInfo from './gameInfo'

const SHCreate = combineReducers({
    gameInfo,
    form
})

export default SHCreate
