
import React from 'react'
import { Table, Column, Cell } from 'fixed-data-table';

import 'fixed-data-table/dist/fixed-data-table.css'
import './Track.css';

const rows = [
	["player1", ['a1', 'b1', 'c1']],
	["player2", ['a2', 'b2', 'c2']],
	["player3", ['a3', 'b3', 'c3']],
];

const Track = ({data}) => {
	if (data === undefined)
		data = rows;
	console.log(data);
	return (
	<Table
	    rowHeight={50}
	    rowsCount={Object.keys(data).length}
	    width={window.innerWidth}
	    height={window.innerHeight}
	    headerHeight={50}>
	    <Column
		    header={<Cell>Player</Cell>}
		    cell={({rowIndex, ...props}) => (
		    	<Cell {...props}>{data.player[rowIndex][0]}</Cell>
		    )}
		    width={200}
	    />
	    for (const)
	    <Column
	        header={<Cell>Objectives</Cell>}
	        cell={({rowIndex, ...props}) => (
	        	<Cell {...props}>
	          		{data.player[rowIndex][1][0/*objective number*/]}
	        	</Cell>
	        )}
	      width={2000}
	    />
  	</Table>
	)
}

export default Track;
