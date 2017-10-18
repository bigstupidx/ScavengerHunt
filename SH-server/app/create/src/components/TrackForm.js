import React from 'react';
import { Field, reduxForm } from 'redux-form';
import PropTypes from 'prop-types';

import './Form.css';

const TrackForm = ({ handleSubmit }) => {
    return (
    <div className='track-form'>
        <div className='mapBg' />
        <form onSubmit={ handleSubmit } className='create-form'>
            <div className='user-prompt'>
                Enter the 4 character game key to track its progress.
            </div>
            <div>
                <Field name='key' component='input' type='text' className='field field-name' placeholder="Game key"/>
            </div>
            <button type='submit' className='button -regular -green'>Create!</button>
        </form>
    </div>
    )
}

TrackForm.PropTypes = {
    onSubmit: PropTypes.func.isRequired
}

export default reduxForm({
    form: 'contact'
})(TrackForm)

