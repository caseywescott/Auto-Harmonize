

AutoHarmonizer {

	var
	<>outnotes,
	<>pclist,
	<>outnoteskeynum,
	<>int_history,
	<>availableints,
	<>initweights,
	<>first_last_note_weights,
	<>first_last_note_bass_weights,
	<>first_last_note_lower_middle_weights,
	<>first_last_note_upper_middle_voice_weights,
	<>first_last_note_upper_voice_weights,
	<>tweights,
	<>above_or_below,
	<>score,
	<>noteObject,
	<>notes,
	<>durations,
	<>current_direction,
	<>melodic_interval,
	<>parson_score,
	<>contour_count,
	<>cantus_harmony_intervals,
	<>cantus_harmony_steps,
	<>parallel_weights,
	<>current_cantus_harmony_interval,
	<>last_int,
	<>middle_int,
	<>step_arr,
	<>melodic_directions,
	<>canditate_note,
	<>transition_weights,

	<>weight_table_stack,
	<>stats,

	<>current_time,
	<>cantus_notes_subdivisions,
	<>offset_values,
	<>num_voices_copy,
	<>cantus_scale_degrees,
	<>harmony_scale_degrees,
	<>voice_ordering,
	<>probabilitytable,
	<>harmonicrepetitiontable,
	<>tableorder,
	<>error_index,
	<>contrapuntal_index,
	<>transition_table_index,
	<>maxspan,
	<>hasclimax,
	<>hasclimaxsum,
	<>highestnotes,
	<>hasanticlimax,
	<>lowestnotes,
	<>cantuskeynums,
	<>notecandidates,
	<>note_consecutiverepeats,
	<>numleaps,
	<>currentleadingtoneindexes,
	<>resolvednotes;


	// constructor method
	*new {arg outnotes, int_history, availableints, initweights, first_last_note_weights, tweights, above_or_below, parson_score, cantus_harmony_intervals, cantus_harmony_steps, parallel_weights, step_arr, melodic_directions, transition_weights, notes, durations, contour_count;

		^super.new.init(outnotes, int_history, availableints, initweights, first_last_note_weights, tweights, above_or_below, parson_score, cantus_harmony_intervals, cantus_harmony_steps, parallel_weights, step_arr, melodic_directions, transition_weights, notes, durations,  contour_count)
	}

	init { arg voice_order = 0;

		voice_ordering = voice_order; //

		num_voices_copy = 3;
		note_consecutiverepeats = LinkedList.new;

		// initiate class variables
		cantuskeynums = LinkedList.new;
		outnotes = LinkedList.new;
		pclist = LinkedList.new;
		outnoteskeynum = LinkedList.new;
		int_history = LinkedList.new;
		availableints =[1,2,3,4,5,6,7,8,9,10];
		initweights = [0,0,1,1,1,1,0,1,0,0.1].normalizeSum;
		first_last_note_weights = [1,0,0,1,1,0,0,1,0,0].normalizeSum;
		first_last_note_bass_weights = [0,0,1,0,1,1,0,1,0,1].normalizeSum; //no second inversin triads in classic 3+ counterpoint
		first_last_note_lower_middle_weights = [0,0,1,1,1,1,0,1,0,0].normalizeSum; //no second inversin triads in classic 3+ counterpoint
		first_last_note_upper_middle_voice_weights = [0,0,1,1,1,1,0,1,0,0].normalizeSum; //no second inversion triads in classic 3+ counterpoint
		first_last_note_upper_voice_weights = [0,0,1,1,1,1,0,1,0,0].normalizeSum; //no second inversion triads in classic 3+ counterpoint
		probabilitytable = nil;
		tableorder = nil;
		tweights = initweights;
		above_or_below = 1.neg;
		parson_score=0;
		resolvednotes = 0;
		cantus_harmony_intervals = LinkedList.new;
		cantus_harmony_steps = LinkedList.new;
		parallel_weights=[1,1,1,1,1,1,1,1,1,1];
		step_arr = LinkedList.new;
		melodic_directions = LinkedList.new;
		transition_weights = [1,1,1,1,1,1,1,1,1,1];
		weight_table_stack = LinkedList.new;
		stats = LinkedList.new; //this could be a Dictionary
		harmonicrepetitiontable = LinkedList.new;
		cantus_notes_subdivisions = LinkedList.new;
		cantus_scale_degrees = LinkedList.new;
		offset_values = LinkedList.new;
		//counter_count is specified as follows [contrary motion, oblique, parallel]
		contour_count = [0,0,0];
		numleaps = 0;
		error_index = 0;
		contrapuntal_index = 0;
		transition_table_index = 0;
		maxspan = 0;
		this.initFunctions();
	}

	loadWeightTable{arg pathName, tableorder1;
		var data = File(pathName.asString.standardizePath,"r");
		probabilitytable = data.readAllString.interpret;
		tableorder = tableorder1;
	}

	loadBachWeightTable{arg tableorder1;

probabilitytable = [[[ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.00048692785976478, 0.0, 7.491197842535e-05, 3.7455989212675e-05, 0.00067420780582815, 0.0059555022848153, 0.00022473593527605, 0.013259420181287, 0.0159562514046, 0.024121657052963, 0.22409918345944, 0.1116937598322, 0.2677354108922, 0.091617349614203, 0.17499438160162, 0.013933627987115, 0.008764701475766, 0.030339351262267, 0.0001498239568507, 0.011948460558843, 0.0012360476440183, 0.0007491197842535, 0.00011236796763803, 0.0, 0.0018353434714211, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ]],
				[ [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.99961322761555, 0.00038677238445175, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.9997582789461, 0.0, 0.0002417210539038, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.00055223193741371, 0.0, 0.0, 0.00024543641662832, 0.00046019328117809, 0.0073017333946924, 0.00082834790612057, 0.010308329498389, 0.032213529682467, 0.015155698726799, 0.15864396379813, 0.12689062739684, 0.3475379659457, 0.096364473078693, 0.13385488571867, 0.011750268446081, 0.0099094953213683, 0.036815462494248, 9.2038656235619e-05, 0.0055223193741371, 0.0020862095413407, 0.0023009664058905, 0.00024543641662832, 3.067955207854e-05, 0.00085902745819911, 0.0, 3.067955207854e-05, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.00031816735602927, 0.99968183264397, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ] ],
				[ [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0013092193399099, 0.0, 0.0, 0.0011265375715504, 0.00082206795761783, 0.017841919376446, 0.0037754232127634, 0.015832419924492, 0.020155888442333, 0.022134940932895, 0.17762757276824, 0.11006576543661, 0.30501765923761, 0.087778589696748, 0.14940323955669, 0.01385336743393, 0.0098039215686275, 0.041803677992936, 0.00048715138229205, 0.011874314943369, 0.0031055900621118, 0.0035927414444038, 0.00030446961393253, 0.0, 0.002283522104494, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ] ],
				[ [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.00010640561821664, 0.0, 0.017344115769313, 0.00010640561821664, 0.00063843370929985, 0.00053202809108321, 0.0019153011278996, 0.066397105767185, 0.0045754415833156, 0.039582889976591, 0.022451585443711, 0.027771866354544, 0.17759097680358, 0.11161949350926, 0.065120238348585, 0.13024047669717, 0.18376250266014, 0.0058523090019153, 0.0069163651840817, 0.079910619280698, 0.00085124494573313, 0.024047669716961, 0.0022345179825495, 0.0025537348371994, 0.0017024898914663, 0.00031921685464993, 0.025111725899127, 0.00063843370929985, 0.0, 0.00010640561821664, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ], [ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 ], [ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 ] ]
			];

		tableorder = tableorder1;
	}



	iterate{
		notes.pitchCollection.do({arg current_cantus_note, i;
			current_cantus_note;
		});
	}

	initFunctions{

		~get_n_part_voicingsPC_and_KeynumTable = {arg cantus = PC2(69), voice = 2, firstlast_or_middle = 0, pc = PColl.lydian(\a), new_voice_table_set = [[0,4,7],[2,4,5,7,9],[0,7]];
			var temp_val, outlist4 = LinkedList.new, outlist3 = LinkedList.new, outlist2, outlist = LinkedList.new, pcoutlist, keynumlist = LinkedList.new, voicing_table_first = [0,4,7], tkeynumlist = LinkedList.new, t1,t2,t3,t4,t5,t6, temppcsub,
			voicing_table_first_last = [0,4,7],voicing_table_last = [0,7], maxtablesize, maxtableindex, outpcs = LinkedList.new;

			/*
			find the largest table in the new_voice_table_set to be used later when an index into the new_voice_table_set is out of bounds,
			the largest table is returned
			*/

			new_voice_table_set.do({arg item, i;
				if(i == 0){
					maxtablesize = item.size;
					maxtableindex = i;
				}{
					if(item.size > maxtablesize){
						maxtablesize = item.size;
						maxtableindex = i;
					};
				}
			});

			//get the table to iterate
			if(firstlast_or_middle >= new_voice_table_set.size){

				temp_val = new_voice_table_set[maxtableindex];
			}{
				temp_val = new_voice_table_set[firstlast_or_middle];
			};

			temp_val.do({arg item, i;

				outlist2 = LinkedList.new;
				pcoutlist = LinkedList.new;

				if(item.isArray == false){
					if(voice == 0){
						pcoutlist.add(cantus);
						pcoutlist.add(PC2(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item.neg)[4].keynum));
						[cantus,pc, item];
						outlist2.add(cantus.keynum);
						outlist2.add(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item.neg)[4].keynum);
					}{
						outlist2.add(cantus.keynum);
						[cantus,pc, item];
						outlist2.add(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item)[4].keynum);
						pcoutlist.add(PC2(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item)[4].keynum));

						pcoutlist.add(cantus);
					};
				}{

					if(voice == 0){
						pcoutlist.add(cantus);
						outlist2.add(cantus.keynum);


						t1 = (Array.fill(item.size, { arg i;
							(item.copyRange(0,i).sum);
						}));

						t1.do({arg item2, i2;
							(cantus.keynum + item2);

							pcoutlist.add(cantus);
							outlist2.add(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item2.neg)[4].keynum);
						});
					}{
						if(voice == (item.size )){

							t1 = (Array.fill(item.size, { arg i;
								(item.reverse.copyRange(0,i).sum);
							}));

							t1.reverse.do({arg item2, i2;
								(cantus.keynum + item2);

								pcoutlist.add(PC2(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item2)[4].keynum));
								outlist2.add(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item2)[4].keynum);
							});

							pcoutlist.add(cantus.keynum);
							outlist2.add(cantus.keynum);
						}{
							t1 = item.copyRange(0,voice-1);
							t3 = (Array.fill(t1.size, { arg i;
								t1.reverse.copyRange(0,i).sum;
							}));

							t3.reverse.do({arg item12, i12;

								pcoutlist.add(PC2(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item12)[4].keynum));
								outlist2.add(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item12)[4].keynum);
							});

							pcoutlist.add(cantus);
							outlist2.add(cantus.keynum);
							t4 = item.copyRange(voice, item.size-1);

							t2 = (Array.fill(t4.size, { arg i;
								t4.copyRange(0,i).sum;
							}));

							t2.do({arg item2, i2;

								pcoutlist.add(PC2(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item2.neg)[4].keynum));
								outlist2.add(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item2.neg)[4].keynum);
							});
						};
					};
				};
				outlist4.add(pcoutlist.asArray);
				outlist3.add(outlist2.asArray);
			});

			if(voice = 0){
			}{
			};

			keynumlist = outlist3;


			keynumlist.do({arg item112, i112;

				if(item112.isArray){
					temppcsub = LinkedList.new;
					item112.do({arg item113, i113;

						temppcsub.add(PC2(item113));
					});
					outpcs.add(temppcsub.asArray);
				}{
					outpcs.add(PC2(item112));
				};

			});



			[outpcs.asArray, keynumlist.asArray];
		};

		~get_n_part_voicingsPC_and_Keynum = {arg cantus = PC2(69), voice = 0, firstlast_or_middle = 0, pc = PColl.lydian(\a), new_voice_table_set = [[0,4,7],[2,4,5,7,9],[0,7]];
			var index, temptable;
			temptable = ~get_n_part_voicingsPC_and_KeynumTable.value(cantus, voice, firstlast_or_middle, pc, new_voice_table_set);
			index = Array.fill( temptable[0].size, {arg i; i;}).choose;
			[temptable[0][index],temptable[1][index]];
		};


		~get_differences_between_notes_in_a_chord = {arg arr = [ 69.0, 64.0, 61, 57.0 ];
			var temp = arr, out = LinkedList.new, out1 = LinkedList.new;

			(temp.size-2).do({arg item, i;
				out.add(
					[arr[i], arr.copyToEnd(i+1)].allTuples
				);
			});

			out = out.flatten;
			out.add(arr.copyToEnd(temp.size-2));

			out.do({arg item, i;
				out1.add(
					item[0]-item[1]
				);
			});
			out1;
		};

		~get_intervals_between_notes_in_a_chord = {arg arr = [ 69.0, 64.0, 61, 57.0 ];
			var temp = arr, out = LinkedList.new, out1 = LinkedList.new;

			(temp.size-2).do({arg item, i;
				out.add(
					[arr[i], arr.copyToEnd(i+1)].allTuples
				);
			});

			out = out.flatten;
			out.add(arr.copyToEnd(temp.size-2));

			out.do({arg item, i;
				out1.add(
					PC2(item[0]).getInterval(PC2(item[1]))[2][1];
				);
			});
			out1.asArray;
		};

		~get_table_for_n_voices = {arg currentnote = PC2(69), numvoices = 4, voice_order = 0, cadence = 0, pc = PColl.lydian(\a);
			var out;
			/*
			generate correct table need to allow voicing type first last note distinction
			*/
			case
			{numvoices == 2}{
				out = currentnote.get_two_part_voicingsPC_and_KeynumTable(voice_order, cadence, pc);
			}
			{numvoices == 3}{
				out = currentnote.get_three_part_voicingsPC_and_KeynumTable(voice_order, cadence, pc);
			}
			{numvoices == 4}{
				out = currentnote.get_four_part_voicingsPC_and_KeynumTable(voice_order, cadence, pc);
			}
			{numvoices == 5}{
				out = currentnote.get_five_part_voicingsPC_and_KeynumTable(voice_order, cadence, pc);
			};
			out;
		};



		~get_differences_between_parsons_in_a_chord = {arg arr = [ -1, 0, -1, 1 ];
			var temp = arr, out = LinkedList.new, out3 = [0,0,0];

			(temp.size-2).do({arg item, i;
				out.add(
					[arr[i], arr.copyToEnd(i+1)].allTuples
				);
			});

			out = out.flatten;
			out.add(arr.copyToEnd(temp.size-2));

			out.do({arg item, i;

				case
				{item[0].asInteger != item[1].asInteger}{

					//if oblique
					if( (item[0].asInteger == 0) || (item[1].asInteger == 0)){
						out3[1] = out3[1] +1;
					}{
						//contrary
						out3[0] = out3[0] +1;
					};
				}{
					//parallel
					out3[2] = out3[2] +1;
				};
			});
			[out3, (out3[0]+out3[1])/out3.sum];
		};




		~normal_form_interval = {arg scaledegree1= 4, scaledegree2 = 1;
			if(scaledegree1-scaledegree2 == 3.neg){
				"fourth";
			};
			if(scaledegree1-scaledegree2 == 4){
				"fourth";
			};

			if(scaledegree1-scaledegree2 == 3){
				"fifth";
			};
			if(scaledegree1-scaledegree2 == 4.neg){
				"fifth";
			};

			["scaledegree1", scaledegree1, "scaledegree2", scaledegree2, scaledegree1-scaledegree2];
		};


		~score_keynum_tables = {arg previous = [ 76.0, 73.0, 61, 57.0, 45], currentnote = PC2(\ds, 5), voice_order = 0, cadence = 1, pc = PColl.lydian(\a), v_table_args, previous2 = nil, table;
			var
			//voicing_table = ~get_table_for_n_voices.value(currentnote, previous.size, voice_order, cadence, pc),
			voicing_table = ~get_n_part_voicingsPC_and_KeynumTable.value(currentnote, voice_order, cadence, pc, v_table_args),
			tparson1, tparson2, melodictritoneoutline, skipinthesamedirection, secondskipinsamedirectionnotshorter, tprobtable = table, temptable, consecutiveperfectintervals, hiddenperfectintervals, scaledegreelisttemp, out = LinkedList.new, first = ~get_differences_between_notes_in_a_chord.value(previous), prevscaledegreelist = LinkedList.new, prev_index_diffs, appended_indexes, parallel_count, ptemp, ptemp1, ptemp2, ptemp3, ptemp4, ptemp5, ptemp6, tempvar1, tempvar2, tempkeynums, unpermittedmelodicintervals, nummelodicleaps, tempidxlist, tempidxlist2, ttable1, ttable2, harmonicrepetition, temp_five_chord_scale_degrees = [6,1], isfivechord, leadingtones, leadingtone_resolutions, resolved, consecutiverepeat, topkeynum, isthereaclimax, tempvoicenum,
			notes_for_difference_indexes = [
				[
					[0,1]
				],
				[
					[0,1],
					[0,2],
					[1,2]
				],
				[
					[0,1],
					[0,2],
					[0,3],
					[1,2],
					[1,3],
					[2,3]
				],
				[
					[0,1],
					[0,2],
					[0,3],
					[0,4],
					[1,2],
					[1,3],
					[1,4],
					[2,3],
					[2,4],
					[3,4]
				]
			],
			notes_for_difference_index = notes_for_difference_indexes[previous.size-2];

			//find scale degrees of previous notes
			previous.do({arg item3, i3;
				prevscaledegreelist.add(PC2(item3).getScaleDegree(pc.filterKeynum(item3), pc));
			});

			prev_index_diffs = ~get_differences_between_notes_in_a_chord.value(prevscaledegreelist).flatten;


			voicing_table[0].do({arg item, i;
				scaledegreelisttemp = LinkedList.new;
				tempkeynums = LinkedList.new;

				/*parallel octaves, fifths fourths, tritone in bass flag, tritone count, leap into perfect octave, leap into fifth
				*/
				parallel_count = [0,0,0,0,0,0,0,0];

				item.do({arg item2, i2;

					scaledegreelisttemp.add(item2.getScaleDegree(item2, pc));

					tempkeynums.add(item2.keynum);
				});

				/*
				In order to create a proper cadence, i need to identify the scale degree of the cantus note, then
				remove that from a list of required scales degrees (the 2nd and 7th) if the candidate note(s)' scale degrees are one of the required scale degrees, then flag it as a five chord.
				*/
				isfivechord = 0;
				temp_five_chord_scale_degrees.remove(currentnote.getScaleDegree(currentnote, pc));

				//scaledegreelisttemp.includes(1) &&
				if(scaledegreelisttemp.includes(6)){
					isfivechord = 1;
				};

				leadingtone_resolutions = 0;
				resolved = 0;
				consecutiverepeat = 0;
				leadingtones = LinkedList.new;
				currentleadingtoneindexes = LinkedList.new;

				outnoteskeynum.last.do({arg item3, i3;
					if(PC2(item3).getScaleDegree(PC2(item3), pc) == 6){
						leadingtones.add(item3);
						currentleadingtoneindexes.add(i3);
					};
				});

				if(leadingtones.asArray.isEmpty == false){


					currentleadingtoneindexes.do({arg item151, i151;

						if((tempkeynums.asArray[item151] - outnoteskeynum.last.asArray[item151]).abs == 1){

							leadingtone_resolutions = leadingtone_resolutions + 1;

						};
					});

					resolved =  resolved + leadingtone_resolutions;
				};

				/*
				find unpermitted melodic intervals such as tritones, sevenths and ninths
				*/

				unpermittedmelodicintervals = 0;
				nummelodicleaps = 0;

				(previous-tempkeynums).do({arg item5, i5;
					if(item5 >2){
						nummelodicleaps = nummelodicleaps + 1;
					};

					if((item5.abs == 6) ||
						(item5.abs == 10) ||
						(item5.abs == 11) ||
						(item5.abs == 13)
					){
						unpermittedmelodicintervals = unpermittedmelodicintervals +1;
					};
				});


				/*
				if there have been more than two notes voiced, check to see if the  melodic direction is consistent on any of the lines. if they are then c
				*/

				/*
				find the number of parallel, contrary and oblique motion
				*/


				tparson1 = LinkedList.new;

				(previous-tempkeynums).do({arg item4, i4;

					case
					{item4.asInteger == 0}{
						tparson1.add(0)
					}
					{item4.asInteger.isPositive}{
						tparson1.add(1)
					}
					{item4.asInteger.isNegative}{
						tparson1.add(1.neg)
					};
				});

				tparson2 = LinkedList.new;

				melodictritoneoutline = 0;
				skipinthesamedirection = 0;
				secondskipinsamedirectionnotshorter = 0;
				temptable = LinkedList.new;
				consecutiveperfectintervals = 0;
				hiddenperfectintervals = 0;
				harmonicrepetition = 0;

				if(previous2.isNil == false){
					(previous2-previous).do({arg item4, i4;
						case
						{item4.asInteger == 0}{
							tparson2.add(0)
						}
						{item4.asInteger.isPositive}{
							tparson2.add(1)
						}
						{item4.asInteger.isNegative}{
							tparson2.add(1.neg)
						};
					});

					/*
					Look for melodic outlines of a tritone
					*/

					tparson2.do({arg item4, i4;
						/*
						find outlines in the same direction
						*/
						if(item4 == tparson1[i4]){
							if((previous2[i4] - tempkeynums[i4]).abs == 6){
								melodictritoneoutline = melodictritoneoutline +1;
							};

							/*
							look for skips in the same direction
							*/

							if(((previous2[i4]-previous[i4]).abs > 2) && ((previous[i4]-tempkeynums[i4]).abs > 2)){
								skipinthesamedirection = skipinthesamedirection + 1;

								if((previous2[i4]-previous[i4]).abs < (previous[i4]-tempkeynums[i4]).abs){
									secondskipinsamedirectionnotshorter = secondskipinsamedirectionnotshorter +1;
								};
							};
						};
					});

				};

				if((tempkeynums[previous.size-2] - tempkeynums[previous.size-1]) == 6){

					/*
					i should probably add a count of all tritones in a voicing
					*/
					parallel_count[3] = 1;
				};


				/*
				find the total tritone count and add it to the stat count
				*/

				~get_differences_between_notes_in_a_chord.value(tempkeynums).flatten.do({arg item9, i9;
					if( [6, 18, 30, 42, 54, 66].includes(item9.asInteger) ){
						parallel_count[4] = parallel_count[4] + 1;
					};

				});

				//	"octaves".postcs;

				ptemp = (~get_differences_between_notes_in_a_chord.value(scaledegreelisttemp).flatten.indicesOfEqual(0)++prev_index_diffs.indicesOfEqual(0));

				if(ptemp.size>ptemp.asArray.asSet.size){
					parallel_count[0] = parallel_count[0]+(ptemp.size-ptemp.asArray.asSet.size);
				};

				tempidxlist = LinkedList.new;
				tempidxlist2 = LinkedList.new;

				~get_differences_between_notes_in_a_chord.value(scaledegreelisttemp).flatten.indicesOfEqual(0).asArray.asSet.do({arg item8, i8;

					tempidxlist.add(notes_for_difference_index[item8])
				});

				tempidxlist.asArray.flatten.asSet.do({arg item7, i7;

					if((previous[item7]-tempkeynums[item7]).abs > 2){
						parallel_count[5] = parallel_count[5] + 1;
					};
				});


				//	hidden fifths";

				tempidxlist = LinkedList.new;

				~get_differences_between_notes_in_a_chord.value(scaledegreelisttemp).flatten.indicesOfEqual(4).asArray.asSet.do({arg item8, i8;

					tempidxlist.add(notes_for_difference_index[item8])
				});

				~get_differences_between_notes_in_a_chord.value(scaledegreelisttemp).flatten.indicesOfEqual(3.neg).asArray.asSet.do({arg item8, i8;

					tempidxlist.add(notes_for_difference_index[item8])
				});


				tempidxlist.asArray.flatten.asSet.do({arg item7, i7;

					if((previous[item7]-tempkeynums[item7]).abs > 2){
						parallel_count[6] = parallel_count[6] + 1;
					};
				});


				//	hidden fourths

				tempidxlist2 = LinkedList.new;

				~get_differences_between_notes_in_a_chord.value(scaledegreelisttemp).flatten.indicesOfEqual(4.neg).asArray.asSet.do({arg item8, i8;

					tempidxlist2.add(notes_for_difference_index[item8])
				});

				~get_differences_between_notes_in_a_chord.value(scaledegreelisttemp).flatten.indicesOfEqual(3).asArray.asSet.do({arg item8, i8;

					tempidxlist2.add(notes_for_difference_index[item8])
				});


				tempidxlist2.asArray.flatten.asSet.do({arg item7, i7;

					if((previous[item7]-tempkeynums[item7]).abs > 2){
						parallel_count[7] = parallel_count[7] + 1;
					};
				});

				ptemp1 = (~get_differences_between_notes_in_a_chord.value(scaledegreelisttemp).flatten.indicesOfEqual(4)++prev_index_diffs.indicesOfEqual(4))++
				(~get_differences_between_notes_in_a_chord.value(scaledegreelisttemp).flatten.indicesOfEqual(3.neg)++prev_index_diffs.indicesOfEqual(3.neg));


				~get_differences_between_notes_in_a_chord.value(scaledegreelisttemp).flatten;

				~get_differences_between_notes_in_a_chord.value(scaledegreelisttemp).flatten;

				if(ptemp1.size>ptemp1.asArray.asSet.size){
					parallel_count[1] = parallel_count[1]+(ptemp1.size-(ptemp1.asArray.asSet.size));
				};


				/*

				Fux mentions that perfect fourths are fine within upper voices
				as it is, the current voicing tables don't produce second inversion triads per the counterpoint literature.
				Therefore, you can simply disregard the instances or filter flag fourths
				*/

				tempidxlist = LinkedList.new;

				~get_differences_between_notes_in_a_chord.value(scaledegreelisttemp).flatten.indicesOfEqual(4.neg).asArray.asSet.do({arg item8, i8;

					tempidxlist.add(notes_for_difference_index[item8])
				});

				~get_differences_between_notes_in_a_chord.value(scaledegreelisttemp).flatten.indicesOfEqual(3).asArray.asSet.do({arg item18, i18;

					tempidxlist.add(notes_for_difference_index[item18])
				});

				tempidxlist.asArray.flatten.asSet.do({arg item7, i7;

					if((previous[item7]-tempkeynums[item7]).abs > 2){
						parallel_count[5] = parallel_count[5] + 1;
					};
				});

				ptemp2 = (~get_differences_between_notes_in_a_chord.value(scaledegreelisttemp).flatten.indicesOfEqual(4.neg)++prev_index_diffs.indicesOfEqual(4.neg))++
				(~get_differences_between_notes_in_a_chord.value(scaledegreelisttemp).flatten.indicesOfEqual(3)++prev_index_diffs.indicesOfEqual(3));

				/*
				according to Fux, parallel fourths are allowed in upper voices meaning that any indexes below num_voices-2 should be removed from the arrays before they are tested for duplicates
				*/

				if(ptemp2.size>ptemp2.asArray.asArray.asSet.size){

					if((ptemp2.size-(ptemp2.asArray.asSet.size)> 0)
					){
						//	"FOURTH DETECTED INDEXES".postcs;
						(ptemp2.asArray.asSet).asArray.selectIndices({|item, i| item < 3});
						//"FOURTH DETECTED INDEXES".postcs;

						//the line below counts all fourths between any voices
						parallel_count[2] = parallel_count[2]+(ptemp2.size-(ptemp2.asArray.asSet.size) );

						//only counts fourths that are with the lowest voice

					};

				};

				/*
				the other thing that i can do is find out the parallel perfect consonances and flag them
				*/


				//this checks for perfect intervals moving to another perfect interval
				ptemp3 = ~get_differences_between_notes_in_a_chord.value(scaledegreelisttemp).flatten.selectIndices({|item, i|
					(item === 0) || (item.abs === 4) || (item.abs === 3) });
				ptemp4 = prev_index_diffs.selectIndices({|item, i|
					(item === 0) || (item.abs === 4) || (item.abs === 3) });

				//look for hidden fifths and octaves

				ptemp5 = prev_index_diffs.selectIndices({|item, i|
					(item === 0) || (item === 4) || (item=== 3.neg) });


				ptemp5.do({arg item20, i20;

					tempvar1 = notes_for_difference_indexes[num_voices_copy-2][item20];

					if(tparson1[tempvar1[0]].isNegative == tparson1[tempvar1[1]].isNegative){
						hiddenperfectintervals = hiddenperfectintervals +1;
					}

				});



				ptemp3.do({arg item19, i19;

					if(ptemp4.includes(item19) == true){
						consecutiveperfectintervals = consecutiveperfectintervals + 1;
					};
				});

				if(previous2.isNil == false){

					(previous2-previous).do({arg item14, i14;


						//tprobtable.add(0);

						ttable1 = (previous-previous2).asInteger;
						ttable2 = (tempkeynums-previous).asInteger;

						ttable1.do({arg item17, i17;
							//"temptable stuff"
							[item17, item17+16, ttable2[i17], ttable2[i17]+16];

							if((item17.abs > 16) || (ttable2[i17].abs > 16)  ){
								temptable.add(0);

							}{

								if(probabilitytable != nil){
									temptable.add(
										probabilitytable[tableorder[i17]][item17 + 16][((tempkeynums-previous)[i17]).asInteger+16]
									);
								};
							};
						});
					});
				};

				if(temptable.isEmpty){
					temptable.add(1);
				};


				appended_indexes = prev_index_diffs++~get_differences_between_notes_in_a_chord.value(scaledegreelisttemp).flatten.indicesOfEqual(0);

				/*
				fill in all of the values to factor in  all of the scores/penalties
				parallel octaves, fifths fourths, tritone in bass flag, tritone count, leap into perfect octave, leap into fifth

				parallel_count = [0,0,0,0,0,0,0,0];

				*/
				/*
				count how many consecutive notes repeat
				*/

				if((previous-tempkeynums).asArray.indicesOfEqual(0).isArray){
					if((previous-tempkeynums).asArray.indicesOfEqual(0).size > 1){
						consecutiverepeat = (previous-tempkeynums).asArray.indicesOfEqual(0).size;

					};
				};


				out.add([tempkeynums, parallel_count ++ [unpermittedmelodicintervals, nummelodicleaps, melodictritoneoutline, skipinthesamedirection, secondskipinsamedirectionnotshorter, consecutiveperfectintervals, hiddenperfectintervals, harmonicrepetition, ~get_differences_between_parsons_in_a_chord.value(tparson1.asArray)[1], isfivechord, resolved, consecutiverepeat], previous,(tempkeynums[0]-tempkeynums.last).abs, i, temptable.sum, temptable, isfivechord, consecutiverepeat
				]);
			});
			out;
		};

		~evaluatescores = {arg scores, filtered_params = [], filtered_params_thresholds = [], probability_params = [5,6,7,8,9,10,11,12,13,14,15], penalty_weights = [];
			var outindexes = LinkedList.new, outlist = LinkedList.new, finalout = LinkedList.new, minspan, minspanidx,tempsort, probability_table, probability_values = LinkedList.new, probability_vals = [5,6,7,8,9,10,11,12,13], chosen_index, penalty_threshold_count, filtered_params_below = LinkedList.new, filtered_params_below_thresholds = LinkedList.new, filtered_params_above = LinkedList.new, filtered_params_above_thresholds = LinkedList.new, tempvarr, tempvarr2, historic_low_penalty_threshold_count = nil, historic_low_penalty_threshold_count_index = nil;
			tempsort = scores.asCollection;

			/*
			separate the positive and negative filter parameters into

			*/

			tempvarr = (filtered_params_thresholds.selectIndices({|item, i| item.isPositive}));
			tempvarr2 = (filtered_params_thresholds.selectIndices({|item, i| item.isPositive == false}));

			if(tempvarr.isEmpty == false){

				tempvarr.do({arg item2, i2;
					filtered_params_below.add(filtered_params[item2]);
					filtered_params_below_thresholds.add(filtered_params_thresholds[item2]);
				});
			};

			if(tempvarr2.isEmpty == false){

				tempvarr2.do({arg item3, i3;
					filtered_params_above.add(filtered_params[item3]);
					filtered_params_above_thresholds.add(filtered_params_thresholds[item3]);
				});
			};


			probability_table = LinkedList.new;

			tempsort.do({arg item, i;

				penalty_threshold_count = 0;

				if(filtered_params.isEmpty){

					if(penalty_weights.isEmpty){

						probability_table.add(item[1][probability_params].sum);
					}{
						probability_table.add( ( (item[1][probability_params]).asArray*penalty_weights ).sum);
					};

					outindexes.add(i);
				}{
					/*
					i need to separate the positive and negative filter flags
					*/

					if(filtered_params_below.isEmpty == false){

						(filtered_params_below_thresholds-(item[1][filtered_params_below.asArray])).do({arg item13, i13;
							if(item13.isNegative){
								penalty_threshold_count = penalty_threshold_count + 1;
							};
						});
					};

					if(filtered_params_above.isEmpty == false){
						((item[1][filtered_params_above.asArray]) - (filtered_params_above_thresholds.asArray.abs) ).do({arg item14, i14;
							if(item14.isNegative){
								penalty_threshold_count = penalty_threshold_count + 1;
							};
						});
					};

					if(historic_low_penalty_threshold_count.isNil){
						historic_low_penalty_threshold_count = penalty_threshold_count;
						historic_low_penalty_threshold_count_index = i;
					}{
						if(historic_low_penalty_threshold_count > penalty_threshold_count){
							historic_low_penalty_threshold_count = penalty_threshold_count;
							historic_low_penalty_threshold_count_index = i;
						}{

						};

					};

					if( penalty_threshold_count == 0){

						/*
						score the total of leaps into perfect octaves and fifths
						*/

						if(penalty_weights.isEmpty){

							probability_table.add(item[1][probability_params].sum);
						}{
							probability_table.add((item[1][probability_params])*penalty_weights.sum);

						};


						//climax note ratio
						outindexes.add(i);
						//	outlist.add(item);
					};
				};

			});


			if(outindexes.isEmpty){

				//"After filtering candidates none were below the parameter filter thresholds...adding some";
				outindexes = [historic_low_penalty_threshold_count_index]; //Array.rand(tempsort.size/2, 0, tempsort.size);
			};

			if(outindexes.size == 1){
				chosen_index = outindexes[0];
			}{
				chosen_index = outindexes.asArray.choose;

			};
			error_index = error_index + tempsort[chosen_index][1][probability_params].sum;
			contrapuntal_index = contrapuntal_index + tempsort[chosen_index][1][16];
			numleaps = numleaps + tempsort[chosen_index][1][9];
			transition_table_index = transition_table_index + tempsort[chosen_index][5];
			maxspan = maxspan + tempsort[chosen_index][3];
			tempsort[chosen_index][0];
		};

		~evaluatescores1 = {arg scores;
			var outindexes = LinkedList.new, outlist = LinkedList.new, finalout = LinkedList.new, minspan, minspanidx,tempsort, probability_table, probability_values = LinkedList.new;
			tempsort = scores.asCollection;

			tempsort.do({arg item, i;

				probability_table = LinkedList.new;

				item[1].do({arg item2, i2;
					if(item2.reciprocal == inf){
						probability_table.add(1);
					}{
						probability_table.add(item2.reciprocal);
					};
				});

				if( (item[1][[0,1,3]]).sum.asInteger == 0){
					outindexes.add(i);
				};
				probability_values.add(probability_table.sum);

			});


			tempsort[outindexes.asArray[(0..((outindexes.size/2).asInteger)+1).choose]][0];
			tempsort[probability_values.maxIndex][0];

		};

	}

	selectFirstNotesOffsetRules{arg pc, cantus, timeoffset = 0, cadence = 0, filtered_params = [], filtered_params_thresholds = [], probability_params = [], penalty_weights = [], v_table_args11 = nil;
		var first_cantus_note, temp, temp2, temp3, temp4, tempoffset,v_table_args = v_table_args11;

		pclist.add(pc);
		cantuskeynums.add(cantus.keynum);

		if(v_table_args[0][0].isArray == false){
			num_voices_copy = 2;
		}{
			num_voices_copy = v_table_args[0][0].size+1;
		};

		first_cantus_note = cantus;

		if(offset_values.isEmpty){
			tempoffset = timeoffset;
		}{
			tempoffset = timeoffset - offset_values[offset_values.size-1];
		};

		offset_values.add(timeoffset);
		temp = ~get_n_part_voicingsPC_and_Keynum.value(first_cantus_note, voice_ordering, cadence, pc, v_table_args);

		if(outnotes.size > 0){
			if(outnotes.size < 3){
				outnoteskeynum[outnotes.size-1];

				temp2 = 	~score_keynum_tables.value(
					outnoteskeynum[outnotes.size-1], first_cantus_note, voice_ordering, cadence, pc, v_table_args);
			}{
				temp2 = 	~score_keynum_tables.value(
					outnoteskeynum[outnotes.size-1], first_cantus_note, voice_ordering,cadence, pc, v_table_args, outnoteskeynum[outnotes.size-2], probabilitytable, tableorder);
			};

			temp3 =	~evaluatescores.value(temp2.asArray, filtered_params, filtered_params_thresholds, probability_params, penalty_weights);

			temp4 = LinkedList.new;

			temp3.do({arg item, i;
				temp4.add(PC2(item));
			});

			outnotes.add(temp4.asArray);

			outnoteskeynum.add(temp3.asArray);

			if(outnoteskeynum.size == 2){

				(highestnotes - outnoteskeynum.asArray[outnoteskeynum.size-1]).asArray.do({arg item16, i16;

					if(item16.isNegative) {
						highestnotes[i16] = outnoteskeynum.asArray[outnoteskeynum.size-1][i16];
						hasclimax[i16] = 1;
					};

				});

				hasclimaxsum = hasclimax.sum;
			}{
				(highestnotes - outnoteskeynum.asArray[outnoteskeynum.size-1]).asArray.do({arg item17, i17;

					case
					{item17.isNegative} {
						highestnotes[i17] = outnoteskeynum.asArray[outnoteskeynum.size-1][i17];
						hasclimax[i17] = 1;
					};

					if(item17 == 0){
						hasclimax[i17] = 0;
					};

				});

				hasclimaxsum = hasclimax.sum;
			};


		}{
			outnotes.add(temp[0]);
			outnoteskeynum.add(temp[1]);

			hasclimax = Array.fill(temp[1].size, {arg i; 0});
			highestnotes = temp[1];

			hasanticlimax = Array.fill(temp[1].size, {arg i; 0});
			lowestnotes = temp[1];
		};
	}

	outNotesToMelody{
		var num_voices = outnotes[0].size, melodyArr = LinkedList.new;

		outnotes.flop.do({arg item, i;
			//melodyArr.add(Melody.new(PColl.new(item), offset_values));
			//	melodyArr.add(Melody.new(PColl.new(item), Array.fill(offset_values.size, {1})));
			melodyArr.add(Melody.new(PColl.new(item), offset_values  ));

		});
		^melodyArr;
	}



	/*
	This is the function to select a new note by looking to see where in the harmonization process it is
	*/

	selectNextNote{arg pc; /* pass pitchCollection as arg to change chords during harmonization iteration */
		var current_cantus_note, i, weight_table_stack = LinkedList.new, out_int;

		if(outnotes.isEmpty){this.selectFirstNote(pc)}
		{
			if(outnotes.asArray.size >= notes.pitchCollection.size){
				//"Harmonization Is Complete";
			}{

				current_cantus_note = notes[outnotes.size];
				i = outnotes.size;

				this.createNoteCanditatesFromAvailableInts(pc);

				out_int = availableints.wchoose(
					(
						this.permittedMelodicIntervals*
						this.avoidLeapingIntoPerfectConsonance(pc)*
						this.harmonicRepetitionTable(pc)*
						this.leapInOneDirectionFollowInOppositeDirection(pc)*
						this.harmonicTritoneInThreeNotesAversionTable(pc)*
						this.parallelTable2(pc)*
						this.harmonicTritoneAversionTable(pc)*
						initweights).normalizeSum
				);

				int_history.add(out_int);

				outnotes.add(
					current_cantus_note.finalModalTranspose(pc.filterKeynum(current_cantus_note.keynum), pc, (out_int-1)*above_or_below)[4];
				);
				(current_cantus_note.finalModalTranspose(pc.filterKeynum(current_cantus_note.keynum), pc, (out_int-1)*above_or_below)[4]);


				//create a list of the intervals between cantus and harmony

				if(above_or_below.isPositive){
					cantus_harmony_intervals.add(
						(notes[0].finalModalTranspose(pc.filterKeynum(current_cantus_note.keynum),
							pc,
							(out_int-1)*above_or_below)[4].keynum)-pc.filterKeynum(current_cantus_note.keynum).keynum
					);

				}{
					cantus_harmony_intervals.add(
						pc.filterKeynum(current_cantus_note.keynum).keynum-(current_cantus_note.finalModalTranspose(pc.filterKeynum(current_cantus_note.keynum),
							pc,
							(out_int-1)*above_or_below)[4].keynum)
					);
				};

				/*
				"cantus_harmony_intervals".postcs;
				["cantus", current_cantus_note.keynum, "harmony",
				(current_cantus_note.finalModalTranspose(pc.filterKeynum(current_cantus_note.keynum),
				pc,
				(out_int-1)*above_or_below)[4].keynum), cantus_harmony_intervals].postcs;
				*/

				//outnotes.last is the current_harmony_note_choice
				/*
				["current_harmony_note_choice", outnotes.last.keynum, "previous_harmony_note_choice", outnotes[outnotes.size-2].keynum, "direction of harmony melody", outnotes.last.keynum-outnotes[outnotes.size-2].keynum, "current cantus note", current_cantus_note.keynum, "previous cantus note", notes[outnotes.size-1].keynum].postcs;
				*/
				//update contour_count = [0,0,0];

				if(
					//oblique motion
					(current_cantus_note.keynum-notes[outnotes.size-2].keynum == 0) ||
					(outnotes.last.keynum-outnotes[outnotes.size-2].keynum == 0)
				){
					contour_count[2] = contour_count[2]+1;
				}{

					if(
						//parallel motion
						(outnotes.last.keynum-outnotes[outnotes.size-2].keynum).isPositive ==
						(current_cantus_note.keynum - notes[outnotes.size-1].keynum).isPositive
					){
						contour_count[1] = contour_count[1]+1;
					}{//contrary motion
						contour_count[0] = contour_count[0]+1;
					};

				};


			};
		};
		weight_table_stack;
	}






	/*
	This is the function to select a new note by looking to see where in the harmonization process it is
	*/




	createNoteCanditatesFromAvailableInts{arg pc;
		var out = LinkedList.new, current_cantus_note = notes[outnotes.size];

		availableints.do({arg ints, ii;
			out.add(current_cantus_note.finalModalTranspose(pc.filterKeynum(current_cantus_note.keynum), pc, (ints-1)*above_or_below)[4].keynum);
		});

		notecandidates = out;
		^out;
	}
	createNoteCanditatesFromAvailableIntsOffset{arg pc, cantus_note;
		var out = LinkedList.new, current_cantus_note = cantus_note.postcs;

		availableints.do({arg ints, ii;
			out.add(current_cantus_note.finalModalTranspose(pc.filterKeynum(current_cantus_note.keynum), pc, (ints-1)*above_or_below)[4].keynum);
		});

		notecandidates = out;
		^out;
	}

	weigthTableStackMultNormalize{
		var out = weight_table_stack.first;

		(weight_table_stack.size-1).do({arg inc;
			out = out*weight_table_stack.asArray[inc+1];
		});

		"normalized weight matrix".postcs;
		^out.asArray;
	}


	parallelTable2{arg pc;
		var current_cantus_harmony_interval,
		current_cantus_note = notes[outnotes.size].postcs,
		parallel_table = Array.fill(notecandidates.size,1);

		notecandidates.do({arg canditate_note, ii;

			["canditate_note", canditate_note, "current_cantus_note", current_cantus_note, pc.filterKeynum(current_cantus_note.keynum).keynum,"test", (canditate_note - (pc.filterKeynum(current_cantus_note.keynum).keynum)).abs].postcs;

			current_cantus_harmony_interval = (canditate_note - (pc.filterKeynum(current_cantus_note.keynum).keynum)).abs;

			["current_cantus_harmony_interval", current_cantus_harmony_interval].postcs;
			cantus_harmony_intervals.last;

			if(cantus_harmony_intervals.last == current_cantus_harmony_interval){

				["cantus_harmony_intervals.last matches current_cantus_harmony_interval", cantus_harmony_intervals.last, current_cantus_harmony_interval].postcs;

				case
				{ current_cantus_harmony_interval.asInteger == 0 } {parallel_table[ii] = 0;"parallel unison".postcs;}
				{ current_cantus_harmony_interval.asInteger == 5 } {parallel_table[ii] = 0; "parallel 4ths".postcs;}
				{ current_cantus_harmony_interval.asInteger == 7 } {parallel_table[ii] = 0; "parallel 5ths".postcs; }
				{ current_cantus_harmony_interval.asInteger == 12} {parallel_table[ii] = 0; "parallel octaves".postcs;};

			}{
				//remove the chance of two perfect intervals in a row - aka direct motion
				if(
					(cantus_harmony_intervals.last == 7) && (current_cantus_harmony_interval.asInteger == 5)
				){
					parallel_table[ii] = 0; "direct motion".postcs;
				};


				case
				{ (cantus_harmony_intervals.last == 7) && (current_cantus_harmony_interval.asInteger == 5) } {parallel_table[ii] = 0; "5th to 4th".postcs;}
				{ (cantus_harmony_intervals.last == 5) && (current_cantus_harmony_interval.asInteger == 7) } {parallel_table[ii] = 0; "4th to 5th".postcs;}
				{ (cantus_harmony_intervals.last == 12) && (current_cantus_harmony_interval.asInteger == 7) } {parallel_table[ii] = 0; "4th to 5th".postcs;}
				{ (cantus_harmony_intervals.last == 7) && (current_cantus_harmony_interval.asInteger == 12) } {parallel_table[ii] = 0; "4th to 5th".postcs;}
				{ (cantus_harmony_intervals.last == 0) && (current_cantus_harmony_interval.asInteger == 12) } {parallel_table[ii] = 0; "4th to 5th".postcs;}
				{ (cantus_harmony_intervals.last == 12) && (current_cantus_harmony_interval.asInteger == 0) } {parallel_table[ii] = 0; "4th to 5th".postcs;}
				{ current_cantus_harmony_interval.asInteger == 12} {parallel_table[ii] = 0; "parallel octaves".postcs;};

			};

		});
		"parallel_table !!!".postcs;
		^parallel_table.postcs;
	}



	parallelTable2Offset{arg pc;
		var current_cantus_harmony_interval,
		current_cantus_note = cantus_notes_subdivisions[outnotes.size].postcs,
		parallel_table = Array.fill(notecandidates.size,1);

		notecandidates.do({arg canditate_note, ii;

			["canditate_note", canditate_note, "current_cantus_note", current_cantus_note, pc.filterKeynum(current_cantus_note.keynum).keynum,"test", (canditate_note - (pc.filterKeynum(current_cantus_note.keynum).keynum)).abs].postcs;
			current_cantus_harmony_interval = (canditate_note - (pc.filterKeynum(current_cantus_note.keynum).keynum)).abs;

			["current_cantus_harmony_interval", current_cantus_harmony_interval].postcs;
			cantus_harmony_intervals.last;

			if(cantus_harmony_intervals.last == current_cantus_harmony_interval){

				["cantus_harmony_intervals.last matches current_cantus_harmony_interval", cantus_harmony_intervals.last, current_cantus_harmony_interval].postcs;

				case
				{ current_cantus_harmony_interval.asInteger == 0 } {parallel_table[ii] = 0;"parallel unison".postcs;}
				{ current_cantus_harmony_interval.asInteger == 5 } {parallel_table[ii] = 0; "parallel 4ths".postcs;}
				{ current_cantus_harmony_interval.asInteger == 7 } {parallel_table[ii] = 0; "parallel 5ths".postcs; }
				{ current_cantus_harmony_interval.asInteger == 12} {parallel_table[ii] = 0; "parallel octaves".postcs;};

			}{
				//remove the chance of two perfect intervals in a row - aka direct motion
				if(
					(cantus_harmony_intervals.last == 7) && (current_cantus_harmony_interval.asInteger == 5)
				){
					parallel_table[ii] = 0; "direct motion".postcs;
				};


				case
				{ (cantus_harmony_intervals.last == 7) && (current_cantus_harmony_interval.asInteger == 5) } {parallel_table[ii] = 0; "5th to 4th".postcs;}
				{ (cantus_harmony_intervals.last == 5) && (current_cantus_harmony_interval.asInteger == 7) } {parallel_table[ii] = 0; "4th to 5th".postcs;}
				{ (cantus_harmony_intervals.last == 12) && (current_cantus_harmony_interval.asInteger == 7) } {parallel_table[ii] = 0; "4th to 5th".postcs;}
				{ (cantus_harmony_intervals.last == 7) && (current_cantus_harmony_interval.asInteger == 12) } {parallel_table[ii] = 0; "4th to 5th".postcs;}
				{ (cantus_harmony_intervals.last == 0) && (current_cantus_harmony_interval.asInteger == 12) } {parallel_table[ii] = 0; "4th to 5th".postcs;}
				{ (cantus_harmony_intervals.last == 12) && (current_cantus_harmony_interval.asInteger == 0) } {parallel_table[ii] = 0; "4th to 5th".postcs;}
				{ current_cantus_harmony_interval.asInteger == 12} {parallel_table[ii] = 0; "parallel octaves".postcs;};

			};

		});
		"parallel_table !!!".postcs;
		^parallel_table.postcs;
	}

	harmonicTritoneAversionTable{arg pc;
		var melodic_interval,
		current_cantus_harmony_interval,
		current_cantus_note = notes[outnotes.size].postcs,
		tritone_table = Array.fill(notecandidates.size,1);

		notecandidates.do({arg canditate_note, inc;

			["canditate_note", canditate_note, "current_cantus_note", current_cantus_note, pc.filterKeynum(current_cantus_note.keynum).keynum,"test", (canditate_note - (pc.filterKeynum(current_cantus_note.keynum).keynum)).abs].postcs;

			current_cantus_harmony_interval = (canditate_note - (pc.filterKeynum(current_cantus_note.keynum).keynum)).abs;

			["current_cantus_harmony_interval", current_cantus_harmony_interval].postcs;
			cantus_harmony_intervals.last;

			if(current_cantus_harmony_interval == 6){
				"tritone averted!".postcs;

				//if there is a tritone remove the chance that it will be selected.
				tritone_table[inc]=0;
			};

		});

		"Tritone Aversion Table !!!".postcs;
		^tritone_table.postcs;
	}

	harmonicTritoneAversionTableOffset{arg pc;
		var melodic_interval,
		current_cantus_harmony_interval,
		current_cantus_note = cantus_notes_subdivisions[outnotes.size].postcs,
		tritone_table = Array.fill(notecandidates.size,1);

		notecandidates.do({arg canditate_note, inc;

			["canditate_note", canditate_note, "current_cantus_note", current_cantus_note, pc.filterKeynum(current_cantus_note.keynum).keynum,"test", (canditate_note - (pc.filterKeynum(current_cantus_note.keynum).keynum)).abs].postcs;

			current_cantus_harmony_interval = (canditate_note - (pc.filterKeynum(current_cantus_note.keynum).keynum)).abs;

			["current_cantus_harmony_interval", current_cantus_harmony_interval].postcs;
			cantus_harmony_intervals.last;

			if(current_cantus_harmony_interval == 6){
				"tritone averted!".postcs;

				//if there is a tritone remove the chance that it will be selected.
				tritone_table[inc]=0;
			};

		});

		"Tritone Aversion Table !!!".postcs;
		^tritone_table.postcs;
	}

	//avoid interval of a tritone in three notes

	harmonicTritoneInThreeNotesAversionTable{arg pc;
		var melodic_interval,
		current_cantus_harmony_interval,
		current_cantus_note = notes[outnotes.size].postcs,
		tritone_table = Array.fill(notecandidates.size,1);

		if(outnotes.size < 2){
			"Not Enough notes for repetition".postcs;
			^tritone_table.postcs;
		}{
			notecandidates.do({arg canditate_note, inc;

				current_cantus_harmony_interval = (canditate_note - (pc.filterKeynum(current_cantus_note.keynum).keynum)).abs.postcs;

				if((canditate_note - outnotes[outnotes.size-2].keynum).abs == 6){

					if((canditate_note - outnotes[outnotes.size-2].keynum).isPositive && ((outnotes[outnotes.size-1].keynum < canditate_note) && (outnotes[outnotes.size-1].keynum > outnotes[outnotes.size-2].keynum))){
						"harmonicTritoneInThreeNotesAverted".postcs;
						tritone_table[inc]=0;
					};

					if((canditate_note - outnotes[outnotes.size-2].keynum).isNegative && ((outnotes[outnotes.size-1].keynum > canditate_note) && (outnotes[outnotes.size-1].keynum < outnotes[outnotes.size-2].keynum) )){
						"harmonicTritoneInThreeNotesAverted".postcs;
						tritone_table[inc]=0;
					};
				};
			});
		};
		"Three Note Tritone Aversion Table".postcs;
		^tritone_table.postcs;
	}

	harmonicTritoneInThreeNotesAversionTableOffset{arg pc;
		var melodic_interval,
		current_cantus_harmony_interval,
		current_cantus_note = cantus_notes_subdivisions[outnotes.size].postcs,
		tritone_table = Array.fill(notecandidates.size,1);

		if(outnotes.size < 2){
			"Not Enough notes for repetition".postcs;
			^tritone_table.postcs;
		}{
			notecandidates.do({arg canditate_note, inc;

				current_cantus_harmony_interval = (canditate_note - (pc.filterKeynum(current_cantus_note.keynum).keynum)).abs.postcs;

				if((canditate_note - outnotes[outnotes.size-2].keynum).abs == 6){

					if((canditate_note - outnotes[outnotes.size-2].keynum).isPositive && ((outnotes[outnotes.size-1].keynum < canditate_note) && (outnotes[outnotes.size-1].keynum > outnotes[outnotes.size-2].keynum))){
						"harmonicTritoneInThreeNotesAverted".postcs;
						tritone_table[inc]=0;
					};

					if((canditate_note - outnotes[outnotes.size-2].keynum).isNegative && ((outnotes[outnotes.size-1].keynum > canditate_note) && (outnotes[outnotes.size-1].keynum < outnotes[outnotes.size-2].keynum) )){
						"harmonicTritoneInThreeNotesAverted".postcs;
						tritone_table[inc]=0;
					};
				};
			});
		};
		"Three Note Tritone Aversion Table".postcs;
		^tritone_table.postcs;
	}

	/*
	Returns a table where any leap in one direction if followed by candidates in the opposite direction

	*/

	leapInOneDirectionFollowInOppositeDirection{arg pc;
		var melodic_interval,
		current_cantus_harmony_interval,
		current_cantus_note = notes[outnotes.size].postcs,
		direction_change_table = Array.fill(notecandidates.size,1);

		if(outnotes.size < 2){
			"Not Enough notes for a direction change".postcs;
			^direction_change_table.postcs;
		}{
			notecandidates.do({arg canditate_note, inc;

				//look for leap past a major second
				if((outnotes[outnotes.size-1].keynum - outnotes[outnotes.size-2].keynum).abs > 2){

					if((outnotes[outnotes.size-1].keynum - outnotes[outnotes.size-2].keynum).isPositive){

						if(canditate_note > outnotes[outnotes.size-1].keynum){
							direction_change_table[inc]=0;
						};
					}{
						if(canditate_note < outnotes[outnotes.size-1].keynum){
							direction_change_table[inc]=0;
						};
					};
				};
			});
		};
		"direction_change_table !!!".postcs;
		^direction_change_table.postcs;
	}


	leapInOneDirectionFollowInOppositeDirectionOffset{arg pc;
		var melodic_interval,
		current_cantus_harmony_interval,
		current_cantus_note = cantus_notes_subdivisions[outnotes.size].postcs,
		direction_change_table = Array.fill(notecandidates.size,1);

		if(outnotes.size < 2){
			"Not Enough notes for a direction change".postcs;
			^direction_change_table.postcs;
		}{
			notecandidates.do({arg canditate_note, inc;

				//look for leap past a major second
				if((outnotes[outnotes.size-1].keynum - outnotes[outnotes.size-2].keynum).abs > 2){

					if((outnotes[outnotes.size-1].keynum - outnotes[outnotes.size-2].keynum).isPositive){

						if(canditate_note > outnotes[outnotes.size-1].keynum){
							direction_change_table[inc]=0;
						};
					}{
						if(canditate_note < outnotes[outnotes.size-1].keynum){
							direction_change_table[inc]=0;
						};
					};
				};
			});
		};
		"direction_change_table !!!".postcs;
		^direction_change_table.postcs;
	}

	//avoid staying on a pitch for too long weight matrix

	avoidLeapingIntoPerfectConsonance{arg pc;
		var melodic_interval,
		current_cantus_harmony_interval,
		current_cantus_note = notes[outnotes.size].postcs,
		avoidLeapingIntoPerfectConsonanceTable = Array.fill(notecandidates.size,1);

		if(outnotes.size < 2){
			"Not Enough notes for a leap".postcs;
			^avoidLeapingIntoPerfectConsonanceTable.postcs;
		}{
			notecandidates.do({arg canditate_note, inc;

				//look for leap past a major second
				if((outnotes[outnotes.size-1].keynum - canditate_note).abs > 2){

					case
					{ PC2(68).stepsToInterval((current_cantus_note.keynum - canditate_note).abs) == 0 }
					{avoidLeapingIntoPerfectConsonanceTable[inc] = 0;"parallel unison".postcs;}
					{ PC2(68).stepsToInterval((current_cantus_note.keynum - canditate_note).abs) == 4 }
					{avoidLeapingIntoPerfectConsonanceTable[inc] = 0; "parallel 4ths".postcs;}
					{ PC2(68).stepsToInterval((current_cantus_note.keynum - canditate_note).abs) == 5 }
					{avoidLeapingIntoPerfectConsonanceTable[inc] = 0; "parallel 5ths".postcs; }
					{ PC2(68).stepsToInterval((current_cantus_note.keynum - canditate_note).abs) == 8}
					{avoidLeapingIntoPerfectConsonanceTable[inc] = 0; "parallel octaves".postcs;};

				};
			});
		};

		"avoid Leaping IntoPerfect Consonance Table !!!".postcs;
		^avoidLeapingIntoPerfectConsonanceTable.postcs;
	}


	avoidLeapingIntoPerfectConsonanceOffset{arg pc;
		var melodic_interval,
		current_cantus_harmony_interval,
		current_cantus_note = cantus_notes_subdivisions[outnotes.size].postcs,
		avoidLeapingIntoPerfectConsonanceTable = Array.fill(notecandidates.size,1);

		if(outnotes.size < 2){
			"Not Enough notes for a leap".postcs;
			^avoidLeapingIntoPerfectConsonanceTable.postcs;
		}{
			notecandidates.do({arg canditate_note, inc;

				//look for leap past a major second
				if((outnotes[outnotes.size-1].keynum - canditate_note).abs > 2){

					case
					{ PC2(68).stepsToInterval((current_cantus_note.keynum - canditate_note).abs) == 0 }
					{avoidLeapingIntoPerfectConsonanceTable[inc] = 0;"parallel unison".postcs;}
					{ PC2(68).stepsToInterval((current_cantus_note.keynum - canditate_note).abs) == 4 }
					{avoidLeapingIntoPerfectConsonanceTable[inc] = 0; "parallel 4ths".postcs;}
					{ PC2(68).stepsToInterval((current_cantus_note.keynum - canditate_note).abs) == 5 }
					{avoidLeapingIntoPerfectConsonanceTable[inc] = 0; "parallel 5ths".postcs; }
					{ PC2(68).stepsToInterval((current_cantus_note.keynum - canditate_note).abs) == 8}
					{avoidLeapingIntoPerfectConsonanceTable[inc] = 0; "parallel octaves".postcs;};

				};
			});
		};

		"avoid Leaping IntoPerfect Consonance Table !!!".postcs;
		^avoidLeapingIntoPerfectConsonanceTable.postcs;
	}

	permittedMelodicIntervals{arg pc;
		var melodic_interval,
		current_cantus_harmony_interval,
		current_cantus_note = notes[outnotes.size].postcs,
		permittedMelodicIntervalTable = Array.fill(notecandidates.size,1);

		if(outnotes.size < 1){
			"Not Enough notes to test for melodic intervals".postcs;
			^permittedMelodicIntervalTable.postcs;
		}{
			notecandidates.do({arg canditate_note, inc;
				"Permitted Melodic".postcs;
				PC2(68).stepsToInterval((outnotes.last.keynum - canditate_note).abs).postcs;
				//look for leap past a major second
				//		if((outnotes[outnotes.size-1].keynum - outnotes[outnotes.size-2].keynum).abs > 2){

				case
				//	{ PC2(68).stepsToInterval((outnotes.last.keynum - canditate_note).abs) == 6 }
				//	{permittedMelodicIntervalTable[inc] = 0;"melodic tritone".postcs;}
				{ PC2(68).stepsToInterval((outnotes.last.keynum - canditate_note).abs) == 7 }
				{permittedMelodicIntervalTable[inc] = 0;"melodic minor seventh".postcs;}
				{ PC2(68).stepsToInterval((outnotes.last.keynum - canditate_note).abs) == 8 }
				{permittedMelodicIntervalTable[inc] = 0;"melodic major seventh".postcs;}
				{ PC2(68).stepsToInterval((outnotes.last.keynum - canditate_note).abs) == 12 }//octave
				{permittedMelodicIntervalTable[inc] = 0;"melodic major seventh".postcs;}
				{ PC2(68).stepsToInterval((outnotes.last.keynum - canditate_note).abs) == 9 }
				{permittedMelodicIntervalTable[inc] = 0; "melodic minor 11".postcs;}
				{ PC2(68).stepsToInterval((outnotes.last.keynum - canditate_note).abs) == 10 }
				{permittedMelodicIntervalTable[inc] = 0;"melodic major seventh".postcs;};

				//		};
			});
		};

		"Permitted Melodic Interval Table !!!".postcs;
		^permittedMelodicIntervalTable.postcs;
	}

	permittedMelodicIntervalsOffset{arg pc;
		var melodic_interval,
		current_cantus_harmony_interval,
		current_cantus_note = cantus_notes_subdivisions[outnotes.size].postcs,
		permittedMelodicIntervalTable = Array.fill(notecandidates.size,1);

		if(outnotes.size < 1){
			"Not Enough notes to test for melodic intervals".postcs;
			^permittedMelodicIntervalTable.postcs;
		}{
			notecandidates.do({arg canditate_note, inc;
				"Permitted Melodic".postcs;
				PC2(68).stepsToInterval((outnotes.last.keynum - canditate_note).abs).postcs;
				//look for leap past a major second
				//		if((outnotes[outnotes.size-1].keynum - outnotes[outnotes.size-2].keynum).abs > 2){

				case
				//	{ PC2(68).stepsToInterval((outnotes.last.keynum - canditate_note).abs) == 6 }
				//	{permittedMelodicIntervalTable[inc] = 0;"melodic tritone".postcs;}
				{ PC2(68).stepsToInterval((outnotes.last.keynum - canditate_note).abs) == 7 }
				{permittedMelodicIntervalTable[inc] = 0;"melodic minor seventh".postcs;}
				{ PC2(68).stepsToInterval((outnotes.last.keynum - canditate_note).abs) == 8 }
				{permittedMelodicIntervalTable[inc] = 0;"melodic major seventh".postcs;}
				{ PC2(68).stepsToInterval((outnotes.last.keynum - canditate_note).abs) == 12 }//octave
				{permittedMelodicIntervalTable[inc] = 0;"melodic major seventh".postcs;}
				{ PC2(68).stepsToInterval((outnotes.last.keynum - canditate_note).abs) == 9 }
				{permittedMelodicIntervalTable[inc] = 0; "melodic minor 11".postcs;}
				{ PC2(68).stepsToInterval((outnotes.last.keynum - canditate_note).abs) == 10 }
				{permittedMelodicIntervalTable[inc] = 0;"melodic major seventh".postcs;};

				//		};
			});
		};

		"Permitted Melodic Interval Table !!!".postcs;
		^permittedMelodicIntervalTable.postcs;
	}

	harmonicRepetitionTable{arg pc;
		var melodic_interval,
		current_cantus_harmony_interval,
		current_cantus_note = notes[outnotes.size].postcs,
		harmonic_repetition_table = Array.fill(notecandidates.size,1);

		"notes".postcs;
		notes.postcs;

		if(outnotes.size < 2){
			"Not enough notes for repetition".postcs;
			^initweights;
		}{
			notecandidates.do({arg canditate_note, inc;
				"repetition info".postcs;

				if(PC2(68).stepsToInterval(cantus_harmony_intervals[cantus_harmony_intervals.size-2].asInteger) ==
					PC2(68).stepsToInterval(cantus_harmony_intervals[cantus_harmony_intervals.size-1].asInteger)
				){"Too much repetition".postcs;

					if(PC2(68).stepsToInterval((current_cantus_note.keynum-canditate_note).abs) ==
						PC2(68).stepsToInterval(cantus_harmony_intervals[cantus_harmony_intervals.size-1].asInteger)
					){
						harmonic_repetition_table[inc] = 0;
					};

				};
			});
		};

		"harmonic_repetition_table !!!".postcs;
		^harmonic_repetition_table.postcs;
	}


	harmonicRepetitionTableOffset{arg pc;
		var melodic_interval,
		current_cantus_harmony_interval,
		current_cantus_note = cantus_notes_subdivisions[outnotes.size].postcs,
		harmonic_repetition_table = Array.fill(notecandidates.size,1);

		"notes".postcs;
		//cantus_notes_subdivisions.postcs;

		if(outnotes.size < 2){
			"Not enough notes for repetition".postcs;
			^initweights;
		}{
			notecandidates.do({arg canditate_note, inc;
				"repetition info".postcs;

				if(PC2(68).stepsToInterval(cantus_harmony_intervals[cantus_harmony_intervals.size-2].asInteger) ==
					PC2(68).stepsToInterval(cantus_harmony_intervals[cantus_harmony_intervals.size-1].asInteger)
				){"Too much repetition".postcs;

					if(PC2(68).stepsToInterval((current_cantus_note.keynum-canditate_note).abs) ==
						PC2(68).stepsToInterval(cantus_harmony_intervals[cantus_harmony_intervals.size-1].asInteger)
					){
						harmonic_repetition_table[inc] = 0;
					};

				};
			});
		};

		"harmonic_repetition_table !!!".postcs;
		^harmonic_repetition_table.postcs;
	}





}