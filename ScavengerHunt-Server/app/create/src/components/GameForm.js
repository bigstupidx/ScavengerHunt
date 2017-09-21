import React from 'react';
import { Field, reduxForm } from 'redux-form';
import PropTypes from 'prop-types';

import './GameForm.css';

const GameForm = ({ handleSubmit }) => {
    return (
    <div className='game-form'>
        <div className='mapBg' />
        <form onSubmit={ handleSubmit } className='create-form'>
            <div className='user-prompt'>
                Get your shareable game key by naming and describing your scavanger hunt game!
            </div>
            <div>
                <Field name='name' component='input' type='text' className='field field-name' placeholder="Game Name"/>
            </div>
            <div>
                <Field name='description' component='textarea' type='text' className='field field-description' placeholder="Game Description"/>
            </div>
            <button type='submit' className='button -regular -green'>Create!</button>
        </form>
    </div>
  )
}

GameForm.PropTypes = {
    onSubmit: PropTypes.func.isRequired
}

export default reduxForm({
    form: 'contact'
})(GameForm)

