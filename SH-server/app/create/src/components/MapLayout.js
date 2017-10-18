import React from 'react';
import ReactLoading from 'react-loading';
import { withGoogleMap, GoogleMap, Marker, InfoWindow} from 'react-google-maps';
import AddObjectiveDialog from './AddObjectiveDialog';
import { post } from '../http';
import { ADD_OBJECTIVE_ENDPOINT, DELETE_OBJECTIVE_ENDPOINT } from '../constants';

const googleMapURL="https://maps.googleapis.com/maps/api/js?v=3.27&libraries=places,geometry&key=AIzaSyBMc_L847oGm0yvye5IsYGyQnfWGs1ryq4";

const GoogleMapComponent = withGoogleMap(props => (
    <GoogleMap
        ref={props.onMapLoad}
        defaultZoom={18}
        defaultCenter={{ lat: 43.47, lng: -80.54 }}
        googleMapURL={googleMapURL}
        onClick={props.onMapClick}>

        {props.markers.map(marker => (
            <Marker {...marker}>
                <InfoWindow>
                    <div> {marker.key} <img data={marker.deleteObjective} onClick = {props.remove} src="https://maxcdn.icons8.com/Share/icon/Editing//delete1600.png"/> </div>
                </InfoWindow>
            </Marker>
        ))}

    </GoogleMap>
));

export default class MapLayout extends React.Component {
    constructor(props){
        super(props);
        this.state = {
            markers: [],
            selectedLat: null,
            selectedLng: null,
        }

        this.handleMapClick = this.handleMapClick.bind(this);
        this.handleCloseObjective = this.handleCloseObjective.bind(this);
        this.handleObjectiveSubmit = this.handleObjectiveSubmit.bind(this);
        this.removeObjective = this.removeObjective.bind(this);
    }

    handleMapClick(event){
        this.setState({
            markers: this.state.markers,
            selectedLat: event.latLng.lat(),
            selectedLng: event.latLng.lng(),
        });
    }

    handleCloseObjective(){
        this.setState({
            markers: this.state.markers,
            selectedLat: null,
            selectedLng: null,
        });
    }

    removeObjective(event){
        var id = event.target.getAttribute('data');
        post.call(this, DELETE_OBJECTIVE_ENDPOINT, {game_id: this.props.gameKey, objective_id: id}, (response)=>{
            let updatedMarkers = this.state.markers.slice();
            for(var i = 0; i < updatedMarkers.length; i++){
                if(updatedMarkers[i].deleteObjective == id){
                    updatedMarkers.splice(i, 1);
                }
            }
            this.setState({
                markers: updatedMarkers,
                selectedLat: null,
                selectedLng: null,
            });
            console.log("remove objective");
        });
    }

    handleObjectiveSubmit(objective_form){
        const location = {lat: this.state.selectedLat, lng: this.state.selectedLng};
        const objective = {
            ...objective_form,
            location
        };
        const data = {game_id: this.props.gameKey, objective};

        post.call(this, ADD_OBJECTIVE_ENDPOINT, data, (response)=>{
            console.log('Objective response: ' , response);

            let newMarkers = this.state.markers.slice();
            let marker = {
                position: location,
                key: objective.name,
                deleteObjective: response.id
            };

            newMarkers.push(marker);
            this.setState({
                markers: newMarkers,
                selectedLat: null,
                selectedLng: null,
            });
        });
    }

    render() {
        return (
        <div className = 'GoogleMapComponent' style = {{height: '100vh', width: '100%'}}>
            <GoogleMapComponent
                containerElement={ <div style={{ height: '100%' }}/> }
                mapElement={ <div style={{ height: '100%' }}/> }
                onMapClick = {this.handleMapClick}
                markers = {this.state.markers}
                remove = {this.removeObjective}
            />

            {this.state.selectedLat != null ? (<AddObjectiveDialog lat={this.state.selectedLat} lng={this.state.selectedLng} submit={this.handleObjectiveSubmit} close={this.handleCloseObjective}/>) : (<div></div>)}
        </div>
        );
    }
}
