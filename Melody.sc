
Melody {
	var <>pitchCollection, <>durations;

	*new {arg pitchCollection, durations;

		durations = durations.asArray;

		^super.newCopyArgs(pitchCollection, durations);
	}

	//could add a pitchbase?

	*newFromEnv {arg env;
		var keyarr, pcarr;

		keyarr = (env.levels.asArray).copyRange(1, env.levels.asArray.size-1);
		pcarr = Array.new(keyarr.size);

		keyarr.do({arg data;
			pcarr.add(PC(data));
		});

		^super.newCopyArgs(PColl(pcarr), env.times.asArray);
	}

	//should I add tonic as an argument to this? In this case, PColl is created inside the Method...

	*newInterleaved { arg mel;
		var notearr, durarr;

		notearr = Array.new(mel.size);
		durarr =  Array.new(mel.size);

		mel.do({arg data;
			var pc, dur;
			#pc, dur = data;
			notearr.add(pc);
			durarr.add(dur);
		});

		^super.newCopyArgs(PColl.new(notearr), durarr);
	}

	*notesOnly {arg pitchCollection;
		^super.newCopyArgs(pitchCollection, Array.series(pitchCollection.pitchCollection.asArray.size, 1, 0));
	}

	//add durations argument?
	*rand {arg pitchCollection;
		var durarr;

		durarr = Array.new(pitchCollection.pitchCollection.size);

		(pitchCollection.pitchCollection.size).do({
			durarr.add([0.25, 0.5, 1].choose);
		});

		^super.newCopyArgs(pitchCollection, durarr, 1, 0);
	}

	/* I haven't thought of a reason to use this yet...
	init {
	}
	*/

	at {arg idx;
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^thismel.at(idx);
	}


	put {arg idx, item;
		var newarr;

		newarr = this.getBothInterleavedArrays().put(idx, item);

		this.setBothInterleaved(newarr);
	}

	getTimeDistancesFromZero {
		var newarr, inc = 0, durarr;

		newarr = Array.new(durations.size);
		durations.do({ arg item;
			newarr.add(inc);

			inc = inc + item;
		});

		^newarr;
	}

	getNoteAtTime {arg timeidx;
		var outnote, inc = 0, durarr;

		if(timeidx == 0){
			outnote = pitchCollection[0];
		}{
			durations.do({ arg item, i;

				if( (timeidx >= inc) && (timeidx < (inc+item)) ){
					outnote = pitchCollection[i];
				};
				inc = inc + item;
			});
		};

		if(timeidx>=durations.sum){
			outnote = pitchCollection.last;
			"Time Index Out of Bounds".postcs;
		};

		^outnote;
	}

	//claridy the nextd two methods uses
	getTimeDistancesFromUntilEndofLastNotes {arg idx, item;
		var newarr, inc = 0, durarr, last1, last2;

		newarr = Array.new(durations.size);
		newarr.add(0);

		durations.do({ arg item, i;
			newarr.add(durations.copyRange(0 ,i).sum);
			inc = inc + item;
		});

		last1 = durations[durations.size-1];
		last2 = newarr[newarr.size-1];

		newarr.add(last2+last1);
		^newarr;
	}

	getTimeDistancesFromUntilEndofLastNote {arg idx, item;
		var newarr, inc = 0, durarr, last1, last2;

		newarr = Array.new(durations.size);
		newarr.add(0);

		durations.do({ arg item, i;
			newarr.add(durations.copyRange(0 ,i).sum);
			inc = inc + item;
		});

		^newarr;
	}

	getBothInterleavedArray {
		var both;

		both = Array.new(2);
		both.add(pitchCollection.pitchCollection.asArray);
		both.add(durations);

		^both.lace()
	}

	getBothInterleavedArrays {
		var arrs;

		arrs = Array.new(pitchCollection.pitchCollection.asArray.size);
		(pitchCollection.pitchCollection.asArray).do({
			arg pc, i;
			arrs.add([pc, durations[i]])
		});

		^arrs
	}

	setBothInterleaved { arg mel;
		var notearr, durarr;

		notearr = Array.new(mel.size);
		durarr =  Array.new(mel.size);

		mel.do({arg data;
			var pc, dur;
			#pc, dur = data;
			notearr.add(pc);
			durarr.add(dur);
		});

		this.pitchCollection_(PColl.new(notearr));
		this.durations_(durarr);
	}

	pCollToKeynums {
		var notearr, durarr, mel;

		mel = this.getBothInterleavedArrays();
		notearr = Array.new(mel.size);

		mel.do({arg data;
			var pc, dur;
			#pc, dur = data;
			notearr.add((pc.keynum).asInteger);
		});

		^notearr
	}


	pCollToFreqy {
		var notearr, durarr, mel;

		mel = this.getBothInterleavedArrays();
		notearr = Array.new(mel.size);

		mel.do({arg data;
			var pc, dur;
			#pc, dur = data;
			notearr.add(pc.freq);
		});

		^notearr
	}

	melToEnvF	{
		var pcarr, durarr, startval;

		pcarr = this.pCollToFreqy();
		durarr =  this.durations.asArray();
		startval = [pcarr[0]];

		^Env.new((startval++pcarr), durarr, 'step');
	}


	isSame {arg melody2;

		^((this.durations.asArray() == melody2.durations.asArray()) &&
			(this.pCollToKeynums == melody2.pCollToKeynums)
		);

	}



	pCollToKeynumsSid {
		var notearr, durarr, mel, dict;


		dict = Dictionary[

			(PC(\bn, 2).keynum).asInteger -> 8,
			(PC(\bn, 3).keynum).asInteger -> 16,
			(PC(\bn, 4).keynum).asInteger -> 33,
			(PC(\bn, 5).keynum).asInteger -> 66,
			(PC(\bn, 6).keynum).asInteger -> 133,
			(PC(\bn, 7).keynum).asInteger -> 254,

			(PC(\as, 2).keynum).asInteger -> 8,
			(PC(\as, 3).keynum).asInteger -> 16,
			(PC(\as, 4).keynum).asInteger -> 31,
			(PC(\as, 5).keynum).asInteger -> 62,
			(PC(\as, 6).keynum).asInteger -> 123,
			(PC(\as, 7).keynum).asInteger -> 241,

			(PC(\an, 2).keynum).asInteger -> 7,
			(PC(\an, 3).keynum).asInteger -> 15,
			(PC(\an, 4).keynum).asInteger -> 29,
			(PC(\an, 5).keynum).asInteger -> 58,
			(PC(\an, 6).keynum).asInteger -> 117,
			(PC(\an, 7).keynum).asInteger -> 234,

			(PC(\gs, 2).keynum).asInteger -> 7,
			(PC(\gs, 3).keynum).asInteger -> 14,
			(PC(\gs, 4).keynum).asInteger -> 28,
			(PC(\gs, 5).keynum).asInteger -> 55,
			(PC(\gs, 6).keynum).asInteger -> 110,
			(PC(\gs, 7).keynum).asInteger -> 220,

			(PC(\gn, 2).keynum).asInteger -> 6,
			(PC(\gn, 3).keynum).asInteger -> 13,
			(PC(\gn, 4).keynum).asInteger -> 26,
			(PC(\gn, 5).keynum).asInteger -> 52,
			(PC(\gn, 6).keynum).asInteger -> 104,
			(PC(\gn, 7).keynum).asInteger -> 209,

			(PC(\fs, 2).keynum).asInteger -> 6,
			(PC(\fs, 3).keynum).asInteger -> 12,
			(PC(\fs, 4).keynum).asInteger -> 25,
			(PC(\fs, 5).keynum).asInteger -> 49,
			(PC(\fs, 6).keynum).asInteger -> 98,
			(PC(\fs, 7).keynum).asInteger -> 196,

			(PC(\fn, 2).keynum).asInteger -> 6,
			(PC(\fn, 3).keynum).asInteger -> 12,
			(PC(\fn, 4).keynum).asInteger -> 23,
			(PC(\fn, 5).keynum).asInteger -> 46,
			(PC(\fn, 6).keynum).asInteger -> 93,
			(PC(\fn, 7).keynum).asInteger -> 183,

			(PC(\en, 2).keynum).asInteger -> 5,
			(PC(\en, 3).keynum).asInteger -> 11,
			(PC(\en, 4).keynum).asInteger -> 22,
			(PC(\en, 5).keynum).asInteger -> 44,
			(PC(\en, 6).keynum).asInteger -> 88,
			(PC(\en, 7).keynum).asInteger -> 173,

			(PC(\ds, 2).keynum).asInteger -> 5,
			(PC(\ds, 3).keynum).asInteger -> 10,
			(PC(\ds, 4).keynum).asInteger -> 21,
			(PC(\ds, 5).keynum).asInteger -> 41,
			(PC(\ds, 6).keynum).asInteger -> 83,
			(PC(\ds, 7).keynum).asInteger -> 163,

			(PC(\dn, 2).keynum).asInteger -> 5,
			(PC(\dn, 3).keynum).asInteger -> 10,
			(PC(\dn, 4).keynum).asInteger -> 20,
			(PC(\dn, 5).keynum).asInteger -> 39,
			(PC(\dn, 6).keynum).asInteger -> 78,
			(PC(\dn, 7).keynum).asInteger -> 147,

			(PC(\cs, 2).keynum).asInteger -> 5,
			(PC(\cs, 3).keynum).asInteger -> 9,
			(PC(\cs, 4).keynum).asInteger -> 18,
			(PC(\cs, 5).keynum).asInteger -> 37,
			(PC(\cs, 6).keynum).asInteger -> 74,
			(PC(\cs, 7).keynum).asInteger -> 147,

			(PC(\cn, 2).keynum).asInteger -> 5,
			(PC(\cn, 3).keynum).asInteger -> 9,
			(PC(\cn, 4).keynum).asInteger -> 17,
			(PC(\cn, 5).keynum).asInteger -> 35,
			(PC(\cn, 6).keynum).asInteger -> 70,
			(PC(\cn, 7).keynum).asInteger -> 139

		];


		mel = this.getBothInterleavedArrays();
		notearr = Array.new(mel.size);

		mel.do({arg data;
			var pc, dur;
			#pc, dur = data;
			notearr.add(dict.at((pc.keynum).asInteger));
		});

		^notearr
	}

	pCollToFreqs {
		var notearr, durarr, mel;

		mel = this.getBothInterleavedArrays();
		notearr = Array.new(mel.size);

		mel.do({arg data;
			var pc, dur;
			#pc, dur = data;
			notearr.add(((pc.freq).asInteger));
		});

		^notearr
	}





	pCollHighestToLowest {
		var arr, pcarr;

		arr  = this.pCollToKeynumsHighestToLowest();
		pcarr = Array.new(arr.size);

		arr.do({arg data;
			var key;
			pcarr.add((PC(key)));
		});

		^pcarr
	}

	pCollToKeynumsHighestToLowest {
		var arr;

		arr  = (this.pCollToKeynums()).sort({ arg a, b; a > b });

		^arr
	}

	pCollLowestToHighest {
		var arr, pcarr;

		arr  = this.pCollToKeynumsLowestToHighest();
		pcarr = Array.new(arr.size);

		arr.do({arg data;
			var key;
			pcarr.add((PC(key)));
		});

		^pcarr
	}

	pCollToKeynumsLowestToHighest {
		var arr;

		arr  = (this.pCollToKeynums()).sort;

		^arr
	}

	lowestPC {
		^PC(this.pCollToKeynumsLowestToHighest().asArray[0]);
	}

	lowestKeynum {
		^this.pCollToKeynumsLowestToHighest().asArray[0];
	}

	highestPC {
		^PC(this.pCollToKeynumsHighestToLowest().asArray[0]);
	}

	highestKeynum {
		^this.pCollToKeynumsHighestToLowest().asArray[0];
	}

	melodicInterval{
		^PitchInterval.new((this.highestKeynum() - this.lowestKeynum()), \up)
	}

	duration {
		^durations.sum
	}

	scaleLength {arg scalar;
		var newdur;

		newdur = this.durations;

		this.durations_(newdur*scalar.abs);
	}

	//doesn't maintain tonic yet...
	//
	remove {arg n;
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.keep(n));
	}


	//doesn't maintain tonic yet...

	keep {arg n;
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.keep(n));
	}

	//doesn't maintain tonic yet...

	drop {arg n;
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.drop(n));
	}

	//doesn't maintain tonic yet...

	copyToEnd {arg startidx;
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.copyToEnd(startidx));
	}


	copyFromStart {arg endidx;
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.copyFromStart(endidx));
	}
	//doesn't maintain tonic yet...

	copySeries {arg first, second, last;
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.copySeries(first, second, last));
	}

	//doesn't maintain tonic yet...

	copyRange {arg start, end;
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.copyRange(start, end));
	}

	//doesn't maintain tonic yet...

	permute {arg idx;
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.permute(idx));
	}

	//doesn't maintain tonic yet...

	perfectShuffle {
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.perfectShuffle);
	}

	//doesn't maintain tonic yet...

	pyramid {arg algoidx;
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.pyramid(algoidx));
	}

	//doesn't maintain tonic yet...

	reverse {
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.reverse);
	}

	//doesn't maintain tonic yet...

	rotate {arg n;
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.rotate(n));
	}

	//doesn't maintain tonic yet...

	stutter {arg numRepeats;
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.stutter(numRepeats));
	}

	//doesn't maintain tonic yet...

	scramble {
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.scramble);
	}

	//doesn't maintain tonic yet...

	mirror {
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.mirror);
	}

	//doesn't maintain tonic yet...

	mirror1{
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.mirror1);
	}

	//doesn't maintain tonic yet...

	wrapExtend{arg length;
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.wrapExtend(length));
	}

	//doesn't maintain tonic yet...

	slide{arg windowLength, stepSize;
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.slide(windowLength, stepSize));
	}

	//doesn't maintain tonic yet...

	foldExtend {arg length;
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.foldExtend(length));
	}

	//doesn't maintain tonic yet...

	swap {arg i, j;
		var thismel;

		thismel = this.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel.swap(i, j));
	}

	//doesn't maintain tonic yet...

	lace {arg mel;
		var thismel, thatmel;

		thismel = this.getBothInterleavedArrays();
		thatmel =  mel.getBothInterleavedArrays();

		^Melody.newInterleaved([thismel, thatmel].lace(thismel.size+thatmel.size));
	}

	//doesn't maintain tonic yet...

	append {arg mel;
		var thismel, thatmel;

		thismel = this.getBothInterleavedArrays();
		thatmel =  mel.getBothInterleavedArrays();

		^Melody.newInterleaved(thismel ++ thatmel);
	}

	//question - is unison size == 0


	getPitchIntervals {

		var arrs, newarr;

		arrs = Array.new(pitchCollection.pitchCollection.asArray.size);
		newarr = Array.new(pitchCollection.pitchCollection.asArray.size-1);
		(pitchCollection.pitchCollection.asArray).doAdjacentPairs({
			arg a, b;
			if(a.keynum == b.keynum){
				newarr.add([PitchInterval.new(\perf, 0), "repeat"]);
			}{
				newarr.add(a.distanceFrom(b));
			}
		});

		^newarr
	}

	getParsonsCodeString {
		var arrs, newarr, c, d;

		arrs = Array.new(pitchCollection.pitchCollection.asArray.size);
		newarr = Array.new(pitchCollection.pitchCollection.asArray.size-1);
		(pitchCollection.pitchCollection.asArray).doAdjacentPairs({
			arg a, b;
			if(a.keynum == b.keynum){
				newarr.add("repeat");
			}{
				#c, d = a.distanceFrom(b);
				newarr.add(d);
			}
		});

		^newarr
	}

	/*
	Transposes melody by modal steps.
	Status: Complete. Unoptimized.
	*/
	modaltransp1 {arg toAPitchCollection, scale, steps;
		var  newarr=LinkedList.new,newarr2=LinkedList.new, c, d;

		this.pitchCollection.do({arg data;
			newarr.add(data);
		});

		newarr.do({ arg item, i;
			[i, "transpo"].postcs;
			newarr2.add(item.mtrans1(PC(item.keynum),  toAPitchCollection, scale, steps)).postln;
		});

		^Melody.new(PColl.new(newarr2.asArray), this.durations);
	}



	melody_modal_transposition {arg toAPitchCollection, steps;
		var out_notes = LinkedList.new;
		this.pitchCollection.do({ arg item, i;
			out_notes.add(PC(69).finalModalTranspose(toAPitchCollection.filterKeynum(item.keynum), toAPitchCollection, steps)[4]);
		});
		^Melody.new(PitchCollection.new(out_notes), this.durations);
	}


	getParsonsCodeInt {
		var arrs, newarr, c, d, dval;

		arrs = Array.new(pitchCollection.pitchCollection.asArray.size);
		newarr = Array.new(pitchCollection.pitchCollection.asArray.size-1);
		(pitchCollection.pitchCollection.asArray).doAdjacentPairs({
			arg a, b;
			if(a.keynum == b.keynum){
				newarr.add(0);
			}{
				#c, d = a.distanceFrom(b);
				if("up" == d){dval = 1}{dval = -1};

				newarr.add(dval);
			}
		});

		^newarr
	}


	chromaticSpline {
		var mel, mel1, ll = LinkedList.new, outdur=durations.asArray, outpc=LinkedList.new;

		mel = this.pCollToKeynums();
		mel1 = mel;

		mel.doAdjacentPairs({ arg a, b, i; [(a - b).abs, i].postln;
			if((a - b).abs==2){

				if((a - b).isPositive){
					ll.add([i, a-1,durations[i]/2]);
				}{
					ll.add([i, a+1, durations[i]/2]);
				};
			}{
			};
		});
		//ll.postln;
		((ll.size)).do({arg data, i;
			"yo".postln;
			ll[i].postln;
			mel1 =	mel1.insert(ll[i][0]+1+i, ll[i][1]);
			outdur[ll[i][0]+i] = ll[i][2];
			outdur=outdur.insert(ll[i][0]+1+i, ll[i][2]);
		});
		//mel1.postln;

		mel1.do({arg data, i;
			outpc.add(PC(data));
		});

		//^[PColl(outpc.asArray), mel1, outdur, Melody.new(PColl(outpc.asArray), outdur)]
		^Melody.new(PColl(outpc.asArray), outdur);
	}

	ornamentation {arg ornamentprobability = 1, subprob = [0.6,0.4], toAPitchCollection; //suspension, passing note, chromatic passing note
		var mel, final_pcoll, mel1, ll = LinkedList.new, outdur=durations.asArray, outpc=LinkedList.new, probsample = ornamentprobability.coin,temp;

		if(toAPitchCollection.isArray){
			final_pcoll = toAPitchCollection;
		}{
			final_pcoll = [toAPitchCollection];

		};


		mel = this.pCollToKeynums();
		mel1 = mel;

		mel.doAdjacentPairs({ arg a, b, i; [(a - b).abs, i].postln;

			if(
				((a - b).abs == 2) ||
				((a - b).abs == 3) ||
				((a - b).abs == 4)
			){

				if(probsample){

					if((a - b).abs == 2
					){
						temp = [0,1].wchoose(subprob);
						case
						{temp == 0 } {

							if((a - b).isPositive){
								ll.add([i, a-2,durations[i]/2]);
							}{
								ll.add([i, a+2, durations[i]/2]);
							};

						}
						{ temp == 1 } {

							if((a - b).isPositive){
								ll.add([i, a-1,durations[i]/2]);
							}{
								ll.add([i, a+1, durations[i]/2]);
							};
						};
					};


					if(
						(a - b).abs == 3 ||
						(a - b).abs == 4
					){
						if((a - b).isPositive){


/*
					ll.add([i, PC(a).finalModalTranspose(PC(a), toAPitchCollection, 1.neg)[4].keynum,durations[i]/2, PC(a).finalModalTranspose(PC(a), toAPitchCollection, 1)[4].keynum]);
	*/
							ll.add([i, PC(a).finalModalTranspose(PC(a), final_pcoll[i % (final_pcoll.size-1)], 1.neg)[4].keynum,durations[i]/2, PC(a).finalModalTranspose(PC(a), final_pcoll[i % (final_pcoll.size-1)], 1)[4].keynum]);
				}{
							/*
					ll.add([i, PC(a).finalModalTranspose(PC(a), toAPitchCollection, 1)[4].keynum, durations[i]/2,  PC(a).finalModalTranspose(PC(a), toAPitchCollection, 1)[4].keynum]);
				*/
							ll.add([i, PC(a).finalModalTranspose(PC(a), final_pcoll[i % (final_pcoll.size-1)], 1)[4].keynum, durations[i]/2,  PC(a).finalModalTranspose(PC(a), final_pcoll[i % (final_pcoll.size-1)], 1)[4].keynum]);

						};

					};


				};
			}{
			};

		});

		//ll.postln;
		((ll.size)).do({arg data, i;
			"yo".postln;
			ll[i].postln;
			mel1 =	mel1.insert(ll[i][0]+1+i, ll[i][1]);
			outdur[ll[i][0]+i] = ll[i][2];
			outdur=outdur.insert(ll[i][0]+1+i, ll[i][2]);
		});
		//mel1.postln;

		mel1.do({arg data, i;
			outpc.add(PC(data));
		});

		//^[PColl(outpc.asArray), mel1, outdur, Melody.new(PColl(outpc.asArray), outdur)]
		^Melody.new(PColl(outpc.asArray), outdur);
	}

	suspensionSpline {
		var mel, mel1, ll = LinkedList.new, outdur=durations.asArray, outpc=LinkedList.new;

		mel = this.pCollToKeynums();
		mel1 = mel;

		mel.doAdjacentPairs({ arg a, b, i; [(a - b).abs, i].postln;
			if((a - b).abs==2){

				if((a - b).isPositive){
					ll.add([i, a-2,durations[i]/2]);
				}{
					ll.add([i, a+2, durations[i]/2]);
				};
			}{
			};
		});
		//ll.postln;
		((ll.size)).do({arg data, i;
			"yo".postln;
			ll[i].postln;
			mel1 =	mel1.insert(ll[i][0]+1+i, ll[i][1]);
			outdur[ll[i][0]+i] = ll[i][2];
			outdur=outdur.insert(ll[i][0]+1+i, ll[i][2]);
		});
		//mel1.postln;

		mel1.do({arg data, i;
			outpc.add(PC(data));
		});

		//^[PColl(outpc.asArray), mel1, outdur, Melody.new(PColl(outpc.asArray), outdur)]
		^Melody.new(PColl(outpc.asArray), outdur);
	}

	modalSplinethirdNew {arg toAPitchCollection;
		var mel, mel1, ll = LinkedList.new, outdur=durations.asArray, outpc=LinkedList.new;

		mel = this.pCollToKeynums().postln;
		mel1 = mel;

		mel.doAdjacentPairs({ arg a, b, i; [(a - b).abs, i].postln;
			if(((a - b).abs==3) || ((a - b).abs==4)){

				if((a - b).isPositive){



					ll.add([i, PC(a).finalModalTranspose(PC(a), toAPitchCollection, 1.neg)[4].keynum,durations[i]/2, PC(a).finalModalTranspose(PC(a), toAPitchCollection, 1)[4].keynum]);
				}{
					ll.add([i, PC(a).finalModalTranspose(PC(a), toAPitchCollection, 1)[4].keynum, durations[i]/2,  PC(a).finalModalTranspose(PC(a), toAPitchCollection, 1)[4].keynum]);
				};

			}{
			};
		});
		"ll".postln;

		ll.postln;
		((ll.size)).do({arg data, i;
			"yo".postln;
			ll[i].postln;
			mel1 =	mel1.insert(ll[i][0]+1+i, ll[i][1]);
			outdur[ll[i][0]+i] = ll[i][2];
			outdur=outdur.insert(ll[i][0]+1+i, ll[i][2]);
		});
		//mel1.postln;

		mel1.do({arg data, i;
			outpc.add(PC(data));
		});

		//^[PColl(outpc.asArray), mel1, outdur, Melody.new(PColl(outpc.asArray), outdur)]
		^Melody.new(PColl(outpc.asArray), outdur);
	}

	modalSplinethird {arg toAPitchCollection, scale;
		var mel, mel1, ll = LinkedList.new, outdur=durations.asArray, outpc=LinkedList.new;

		mel = this.pCollToKeynums().postln;
		mel1 = mel;

		mel.doAdjacentPairs({ arg a, b, i; [(a - b).abs, i].postln;
			if(((a - b).abs==3) || ((a - b).abs==4)){

				if((a - b).isPositive){
					ll.add([i, PC(a).mtrans1(PC(a), toAPitchCollection, scale, -1).keynum,durations[i]/2, PC(a).mtrans1(PC(a), toAPitchCollection, scale, 1).keynum]);
				}{
					ll.add([i, PC(a).mtrans1(PC(a), toAPitchCollection, scale, 1).keynum, durations[i]/2,  PC(a).mtrans1(PC(a), toAPitchCollection, scale, 1).keynum]);
				};
			}{
			};
		});
		"ll".postln;

		ll.postln;
		((ll.size)).do({arg data, i;
			"yo".postln;
			ll[i].postln;
			mel1 =	mel1.insert(ll[i][0]+1+i, ll[i][1]);
			outdur[ll[i][0]+i] = ll[i][2];
			outdur=outdur.insert(ll[i][0]+1+i, ll[i][2]);
		});
		//mel1.postln;

		mel1.do({arg data, i;
			outpc.add(PC(data));
		});

		//^[PColl(outpc.asArray), mel1, outdur, Melody.new(PColl(outpc.asArray), outdur)]
		^Melody.new(PColl(outpc.asArray), outdur);
	}

	permuterHarmony {arg index, steparr, pcoll;
		var notes, permutenum, indexes, numcycles, envbp, envt, incenv, key = 1;

		notes = this.pitchCollection.pitchCollection.asArray;

		indexes = Array.new(notes.size);

		permutenum = notes.size**steparr.size;
		numcycles = permutenum/steparr.size;

		'Number of Harmonic Permutations'.postln;

		permutenum.postln;
		//'Number of Harmonic Permutations'.postln;

		//	notes.postln;

		envbp = Array.series(notes.size, 0, 1);

		envt = Array.series(envbp.size-1, 1, 0);

		envbp = envbp.addFirst(0);

		envt = Array.series(envbp.size-1, 1, 0);

		incenv = Env.new(envbp, envt, 'step');

		envbp.postln;
		envt.postln;

		indexes.add((index)%(steparr.size));

		(notes.size-1).do({arg pc, pos;


			indexes.add(((index / (key * steparr.size)).asInteger)%(steparr.size)).postln;
			key = key+1;

		});

		//cycle = every 3 index/cycle.....cycl

		//(index / (cardinality * steparr.size)).asInteger



		//	^[permutenum, "note1index", indexes]
	}

	//tester method....

	addToScore {arg starttime, score;
		var mel, now =0, noteObject,noteObject2;

		noteObject = CtkSynthDef(\test, {arg freq, amp, dur;
			Out.ar(0, SinOsc.ar(freq, 0, Line.kr(amp, 0, dur)))
		});
		noteObject = CtkSynthDef(\test, {arg freq, amp, dur;
			Out.ar(0, SinOsc.ar(freq, 0, Line.kr(amp, 0, dur)))
		});

		mel = this.getBothInterleavedArrays();

		mel.do({arg data;
			var pc, dur;
			#pc, dur = data;
			score.add(noteObject.new(now + starttime, dur).freq_(pc.freq).amp_(0.25).dur_(dur));

			now = now + dur;
		})
	}

	addToScoreSynth {arg starttime, score, synth;
		var mel, now =0, noteObject;

		noteObject = synth;

		mel = this.getBothInterleavedArrays();

		mel.do({arg data;
			var pc, dur;
			#pc, dur = data;
			score.add(noteObject.new(now + starttime, dur).freq_(pc.freq).amp_(0.25).dur_(dur));

			now = now + dur;
		})
	}

	melToEnv	{
		var pcarr, durarr, startval;

		pcarr = this.pCollToKeynums();
		durarr =  this.durations.asArray();
		startval = [pcarr[0]];

		^Env.new((startval++pcarr), durarr, 'step');
	}


	compareMels2Env	{arg mel;
		var env1, env2, onset1, onset2, indexes, intervalArr, levls, startval;

		env1 = this.melToEnv();
		env2 = mel.melToEnv();

		onset1 = this.getTimeDistancesFromUntilEndofLastNote();
		onset2 = mel.getTimeDistancesFromUntilEndofLastNote();

		indexes = ((onset1++onset2).asSet).asArray.sort();
		intervalArr = Array.new(indexes.size);

		indexes.do({arg idx, i;
			var pc, dur;
			intervalArr.add(env1.at(idx)-env2.at(idx));
		});
		startval = [intervalArr.first];
		indexes.add(this.duration);

		levls = Array.new(indexes.size-1);
		indexes.doAdjacentPairs({ arg a, b; levls.add((a - b).abs);							});

		^Env.new((startval++intervalArr), levls, 'step');
	}


	compareMels2EnvDiffSizes	{arg mel;
		var dur1, dur2, env1, env2, onset1, onset2, indexes, intervalArr, levls, startval, smallestDuration, finalidx;

		dur1 = this.duration();
		dur2 = mel.duration();

		smallestDuration = [dur1, dur2].minItem;

		env1 = this.melToEnv();
		env2 = mel.melToEnv();

		onset1 = this.getTimeDistancesFromUntilEndofLastNote();
		onset2 = mel.getTimeDistancesFromUntilEndofLastNote();

		indexes = ((onset1++onset2).asSet).asArray.sort();
		intervalArr = Array.new(indexes.size);

		finalidx = indexes.reject({ arg item, i; item > smallestDuration });

		indexes.do({arg idx, i;
			var pc, dur;
			intervalArr.add(env1.at(idx)-env2.at(idx));
		});

		startval = [intervalArr.first];

		levls = Array.new(finalidx.size-1);
		finalidx.doAdjacentPairs({ arg a, b; levls.add((a - b).abs);							});

		^Env.new((startval++intervalArr), levls, 'step');
	}



	compareMels2StepEnvDiffSizes	{arg mel;
		var dur1, dur2, env1, env2, onset1, onset2, indexes, intervalArr, levls, startval, smallestDuration, finalidx;

		dur1 = this.duration();
		dur2 = mel.duration();

		smallestDuration = [dur1, dur2].minItem;

		env1 = this.melToEnv();
		env2 = mel.melToEnv();

		onset1 = this.getTimeDistancesFromUntilEndofLastNote();
		onset2 = mel.getTimeDistancesFromUntilEndofLastNote();

		indexes = ((onset1++onset2).asSet).asArray.sort();
		intervalArr = Array.new(indexes.size);

		finalidx = indexes.reject({ arg item, i; item > smallestDuration });

		indexes.do({arg idx, i;
			var pc, dur, cc, dd;
			#cc, dd = 	PC(env1.at(idx)).distanceFrom(PC(env2.at(idx)));
			intervalArr.add(cc.size.asInteger);

		});

		startval = [intervalArr.first];

		levls = Array.new(finalidx.size-1);
		finalidx.doAdjacentPairs({ arg a, b; levls.add((a - b).abs);							});

		^Env.new((startval++intervalArr), levls, 'step');
	}


	//in progress ... need env to mel....I think its fine to think of interval arrays as melodies...?!?!?
	/*
	it does make me think of the need to define a melody by an array of intervals...and also be able to return those intervals...that would validate a interval array being treated like a melody
	there should be a method that returns an interval array and a base Pitch...that way one doesnt need to make an intervalCollection Class because an IntervalArray is really a melody with its pitch base ignored....
	*/

	//usefl if melodys are reperesented by interval shape
	transposeByStepEnvDiffSizes	{arg env;
		var mel, dur1, dur2, env1, env2, onset1, onset2, indexes, intervalArr, levls, startval, smallestDuration, finalidx, onset2norm;

		mel = Melody.newFromEnv(env);
		dur1 = this.duration();
		dur2 = mel.duration();

		smallestDuration = [dur1, dur2].minItem;

		env1 = this.melToEnv();
		env2 = mel.melToEnv();

		onset1 = this.getTimeDistancesFromUntilEndofLastNote();
		onset2 = mel.getTimeDistancesFromUntilEndofLastNote();

		indexes = ((onset1++onset2).asSet).asArray.sort();
		intervalArr = Array.new(indexes.size);

		finalidx = indexes.reject({ arg item, i; item > smallestDuration });

		indexes.do({arg idx, i;
			var pc, dur;
			intervalArr.add(env1.at(idx)+env2.at(idx));
		});

		startval = [intervalArr.first];

		levls = Array.new(finalidx.size-1);
		finalidx.doAdjacentPairs({ arg a, b; levls.add((a - b).abs);							});

		^Melody.newFromEnv(Env.new((startval++intervalArr), levls, 'step'));
	}




	cmEnv	{arg mel, offset = 0;
		var dur1, dur2, env1, env2, onset1, onset2, index1, index2, intervalArr, levls, startval, smallestDuration, finalidx, level1, level2;
		var helperarr1, helperarr2, helperarr3, helperarr4, mel1ptbegin, mel2ptbegin, mel1ptend, mel2ptend, times1, times2, onset2norm, normalized1, normalized2, absolutebegin, absoluteend, normindexes, mel2indexes, intervaltimes, overlapdurm, envtimes, overlapdur;

		dur1 = this.duration();
		dur2 = mel.duration();

		smallestDuration = [dur1, offset + dur2].minItem;

		env1 = this.melToEnv();
		env2 = mel.melToEnv();

		level1 = env1.levels();
		level2 = env2.levels();

		index1 = env1.times();
		index2 = env2.times();

		onset1 = this.getTimeDistancesFromUntilEndofLastNote();
		onset2 = mel.getTimeDistancesFromUntilEndofLastNote();

		if(offset.isPositive){

			if(offset >= dur1){

				^"Melodies do not overlap...Offset is larger than the duration of other melody";

			}{
			}
		}{

			if(offset.abs >= dur2){

				^"Melodies do not overlap...Negative offset is larger than the duration of other melody";

			}{
			}
		};

		//if they get past here, then there will be melodic overlap to analyze

		//find the appropriate index points for one of the melodies

		//find the end points in each melody that will be analyzed


		if(offset.isPositive){

			mel1ptbegin = offset;
			mel1ptend = [dur1, offset + dur2].minItem;

			mel2ptbegin = 0;
			mel2ptend = mel1ptend-offset;

		}{

			mel1ptbegin = 0;
			mel1ptend = [dur2-(offset.abs), dur1].minItem;

			mel2ptbegin = offset.abs;
			mel2ptend = offset.abs + mel1ptend;

		};

		/*
		Now that I have the start and end point for both melodies, collect the index val,
		I need to make 'that' melodies values and normalize them so that the first value is 0

		*/

		//collect onsets from mel one....

		times1 = LinkedList.new();
		times2 = LinkedList.new();



		onset1.do({arg item, i;
			var inc = 0;

			if((item >= mel1ptbegin)&&(item < mel1ptend)){

				if(inc == 0){

					times1.addFirst(item);

					inc = inc+1;

				}{

					times1.add(item);

					inc = inc+1;

				}

			}{}

		});


		onset2.do({arg item, i;
			var inc = 0;

			if((item >= mel2ptbegin)&&(item < mel2ptend)){

				if(inc == 0){

					times2.addFirst(item);

					inc = inc+1;
				}{

					times2.add(item);

					inc = inc + 1;
				}

			}{}

		});

		onset2norm = times2.minItem;
		normalized2 = Array.new(times2.size);

		if(offset.isPositive){
			normalized2  = (times2.asArray)+offset;
		}{
			normalized2  = (times2.asArray)+offset;
		};

		normindexes = (times1.asArray++normalized2).asSet.asArray.sort;


		if(offset.isPositive){
			mel2indexes = normindexes-offset;
		}{
			mel2indexes = normindexes-offset;
		};

		intervalArr = Array.new(normindexes.size);

		normindexes.do({arg idx, i;

			intervalArr.add( env2.at(mel2indexes[i]) - env1.at(idx) );

		});

		envtimes = Array.new(normindexes.size-1);

		normindexes.doAdjacentPairs({ arg a, b; envtimes.add((a - b).abs);							});

		overlapdur = normindexes.maxItem - (envtimes.sum);

		envtimes.add(overlapdur);

		//because norm indexes always relates to melpody 1, i can take normindexes and relate that to mel1begin and mel1end







		[
			["offset", offset],
			["onsets", [onset1, onset2]],
			["timeindexes", [index1, index2]],
			["Pitches", [level1, level2]],
			["Melody1 Begin/End point", [mel1ptbegin, mel1ptend]],
			["Melody2 Begin/End point", [mel2ptbegin, mel2ptend]],
			["Melody1 times", times1.asArray.sort],
			["Melody2 times", times2.asArray.sort],
			["Normalized Melody2 times", normalized2.sort],
			["Melody1 interval times", (times1.asArray++normalized2).asSet.asArray.sort],
			["Meldoy2 times", mel2indexes],
			["Norm Indexes", normindexes],
			["Env Times", envtimes],
			["INTERVALS", intervalArr]

		].postcs;

	}


	//return env, offset, env duration
	compareMels2Envoffset	{arg mel, offset;
		var dur1, dur2, newdur, env1, env2, onset1, onset2, indexes, intervalArr, levls, startval, env1offset = 0, env2offset = 2, vv, vv2, bb, bb2;


		//set offset and ending? value

		if(offset.isPositive){
			env1offset = offset;
		}{
			env1offset = offset.abs;
		};

		dur1 = this.duration();
		dur2 = mel.duration();

		env1 = this.melToEnv();
		env2 = mel.melToEnv();

		newdur = (dur1- env1offset);

		onset1 = this.getTimeDistancesFromUntilEndofLastNote();

		// remove all elements below envoffset1
		vv = onset1.reject({ arg item, i; item < env1offset });

		onset2 = mel.getTimeDistancesFromUntilEndofLastNote();

		//remove all elements below envoffset2
		vv2 = onset2.reject({ arg item, i; item < env2offset });

		//then...remove the parts at the ends of both of the melodies...
		//remove anything above (dur - env1offset)
		//remove anything above (dur - env2offset)

		bb = onset1.reject({ arg item, i; item > (dur1 - env1offset) });
		bb2 = onset2.reject({ arg item, i; item > (dur2 - env2offset)});


		indexes = ((bb++bb2).asSet).asArray.sort();
		intervalArr = Array.new(indexes.size);

		indexes.do({arg idx, i;
			var pc, dur;
			intervalArr.add(env1.at(idx+offset)-env2.at(idx));
		});
		startval = [intervalArr.first];
		//this. duration needs to change....
		indexes.add(this.duration);

		levls = Array.new(indexes.size-1);
		indexes.doAdjacentPairs({ arg a, b; levls.add((a - b).abs);							});

		^Env.new((startval++intervalArr), levls, 'step');
	}

	transpose {arg aPitchInterval, direction = \up;
		var pcarr;

		pcarr = (this.pitchCollection).transpose(aPitchInterval, direction);

		^Melody.new(pcarr, this.durations);
	}

	modalTranspose {arg steps, fromAPitchCollection, toAPitchCollection;
		var pcarr;

		pcarr = (this.pitchCollection).modalTranspose(steps, fromAPitchCollection, toAPitchCollection);

		^Melody.new(pcarr, this.durations);

	}
	/*



	modalTranspose {arg aPitchInterval, direction = \up;

	}
	*/

	setTonic {arg tonic;
		this.pitchCollection_(PColl.new(pitchCollection.pitchCollection.asArray, tonic));

	}

	/* This would need an accessor method in PitchClass right?
	getTonic{arg tonic;
	this.pitchCollection);
	}
	*/



	compareMelodies{arg melody2, offset, legal_intervals = [3, 4, 5, 6, 8], pcoll = PColl.lydian(\c);
		var times1 = this.getTimeDistancesFromZero, times2 = melody2.getTimeDistancesFromZero, levels1 = this.pCollToKeynums, levels2 = melody2.pCollToKeynums, start, times1n = times1, times2n = times2, inc1 = 0, inc2 = 0, overlap1, overlap2, dur1 = this.duration, dur2 = melody2.duration, smallestdur, outlevels1 = LinkedList.new, outlevels2 = LinkedList.new,
		outt, outl, overlap1copy, trim1, trim2, trim3, trim4, envBreakpoints1, envBreakpoints2, mel_env_1=this.melToEnv, mel_env_2=melody2.melToEnv, outp, overlap3, overlap4, outenv, finaltimes, finallevels, interval_arr=LinkedList.new, good_interval=0, bad_interval = 0, above_interval=0, below_interval = 0, harmony_above_below, current_interval, voice_crossed = 0, previous_interval, steplist=LinkedList.new, amended_melody_pcs=LinkedList.new, current_amended_step;

		if(offset.isPositive){

			start = offset;
			dur1 = dur1 - offset;
			smallestdur = [dur1, dur2].minItem; //the total duration of the overlapping portion

			//locate the breakpoints for the 1st melody

			//remove breakpoints below offset
			overlap1 = times1n.reject({ arg item, i; item < offset });
			//remove breakpoints outside bounds of overlap
			trim1 = overlap1.reject({ arg item, i; item > (smallestdur+offset) });

			//locate the breakpoints for the 1st melody that correspond to the breakpoints of the 2nd melody

			//remove breakpoints outside the bounds of overlap
			overlap2 = (offset+times2n).reject({ arg item, i; item > (smallestdur+offset) });

			//concatenate both segments from each melody to define the breakpoints for melody1
			envBreakpoints1 = ([offset]++trim1++overlap2).asSet.asArray.sort;


			//locate the breakpoints for the 2nd melody

			//remove breakpoints from second melody outside bounds of overlap
			trim2 = times2n.reject({ arg item, i; item > smallestdur });

			//locate the breakpoints for the 1st melody that correspond to the breakpoints of the 2nd melody
			//remove breakpoints outside the bounds of overlap

			overlap4 = (times1).reject
			(
				{ arg item, i;
					item > smallestdur }
			);

			overlap3 = (overlap4-offset).reject({ arg item, i; item < 0 });

			//concatenate both segments from each melody to define the breakpoints for melody1
			envBreakpoints2 = (trim2++overlap3).asSet.asArray.sort;

		}{

		};



		envBreakpoints1.do({ arg item, i; outlevels1.add(this.melToEnv.at(item)) });
		envBreakpoints2.do({ arg item, i; outlevels2.add(melody2.melToEnv.at(item)) });

		//Melody.new(outlevels1-outlevels2, envBreakpoints2.differentiate).melToEnv;
		outp = envBreakpoints2.differentiate.asArray;
		outp.removeAt(0);

		["smallestdur",smallestdur,times1, times2, envBreakpoints1,envBreakpoints2,envBreakpoints2.differentiate.sum, outp++[smallestdur-(envBreakpoints2.differentiate.sum)], outlevels1-outlevels2].postcs;
		outenv = outlevels1-outlevels2;

		finaltimes = outp++[smallestdur-(envBreakpoints2.differentiate.sum)];
		"finaltimes".postcs;
		finaltimes.postcs;
		"outenv".postcs;
		outenv.postcs;
		outenv[0].postcs;
		finallevels = outenv[0].asArray++outenv.asArray;

		"finallevels".postcs;
		finallevels.postcs;
		//create list of intervals

		outlevels2.do({ arg item, i;

			current_interval = pcoll.distanceFromFilter(PC(item), PC(outlevels1[i]) );

			//current_interval = PC(item).distanceFrom2(PC(outlevels1[i]));

			interval_arr.add(current_interval);
			"current_interval".postcs;
			current_interval.postcs;

			/*
			identify if the harmony is above or below the harmonized melody by tallying positive or negative intervals
			above_interval/below_interval
			Note, distanceFrom2 calculates the difference from the first melody to the second, therefore, if the harmony note is above, the interval to the base melody is down
			*/

			if(current_interval[1].asString == "down".asString)
			{
				above_interval = above_interval+1;
			};

			if(current_interval[1].asString == "up".asString)
			{
				below_interval = below_interval+1;
			};

			/*
			identify if the harmony intervals are acceptable given the specified legal_intervals
			*/

			if(legal_intervals.asArray.includes((current_interval[0]).asInteger)  )
			{
				good_interval=good_interval+1;
				amended_melody_pcs.add(PC(item));
				steplist.add(0);
			}
			{
				bad_interval=bad_interval+1;
				current_amended_step = legal_intervals[legal_intervals.indexIn((current_interval[0]).asInteger)];
				steplist.add(current_amended_step);
				amended_melody_pcs.add( (PC(outlevels1[i]).finalModalTranspose(PC(outlevels1[i]), pcoll, current_amended_step.asInteger))[4]  );
			};


			if((i > 0)){
				"Voice!!!".postcs;
				i.postln;
				current_interval.postcs;
				current_interval[1].asString.postcs;

				if(current_interval[1].asString!=previous_interval[1].asString)
				{
					"a voice crossed!!!!".postcs;
					voice_crossed=1;
				};

			};

			previous_interval = current_interval;

		});

		/*
		If the harmony melody is above the base melody, the value of harmony_above_below == 1, below == -1 or 0 if there are as many notes above as below
		*/

		harmony_above_below = case
		{above_interval>below_interval}{1}
		{above_interval<below_interval}{1.neg}
		{above_interval==below_interval}{0};

		"harmony_above_below".postcs;
		"(above_interval/below_interval)".postcs;
		[above_interval,below_interval].postcs;
		harmony_above_below.postcs;

		^["harmony_above_below", harmony_above_below, Env.new(finallevels, finaltimes.asArray, 'step'), interval_arr, good_interval, bad_interval, good_interval/bad_interval,"voice_crossed",voice_crossed, "steplist", steplist, amended_melody_pcs, Melody.new(PitchCollection.new(amended_melody_pcs), finaltimes.asArray)];

	}

	quantizeHarmony{arg melody2, offset, legal_intervals = [3, 4, 5, 6, 8], pcoll = PColl.lydian(\c);

		var times1 = this.getTimeDistancesFromZero, times2 = melody2.getTimeDistancesFromZero, levels1 = this.pCollToKeynums, levels2 = melody2.pCollToKeynums, start, times1n = times1, times2n = times2, inc1 = 0, inc2 = 0, overlap1, overlap2, dur1 = this.duration, dur2 = melody2.duration, smallestdur, outlevels1 = LinkedList.new, outlevels2 = LinkedList.new,
		outt, outl, overlap1copy, trim1, trim2, trim3, trim4, envBreakpoints1, envBreakpoints2, mel_env_1=this.melToEnv, mel_env_2=melody2.melToEnv, outp, overlap3, overlap4, outenv, finaltimes, finallevels, interval_arr=LinkedList.new, good_interval=0, bad_interval = 0, above_interval=0, below_interval = 0, harmony_above_below, current_interval, voice_crossed = 0, previous_interval, steplist=LinkedList.new, amended_melody_pcs=LinkedList.new, current_amended_step, analysis = this.compareMelodies(melody2, offset, legal_intervals, pcoll);


		if(offset.isPositive){

			start = offset;
			dur1 = dur1 - offset;
			smallestdur = [dur1, dur2].minItem; //the total duration of the overlapping portion

			//locate the breakpoints for the 1st melody

			//remove breakpoints below offset
			overlap1 = times1n.reject({ arg item, i; item < offset });
			//remove breakpoints outside bounds of overlap
			trim1 = overlap1.reject({ arg item, i; item > (smallestdur+offset) });

			//locate the breakpoints for the 1st melody that correspond to the breakpoints of the 2nd melody

			//remove breakpoints outside the bounds of overlap
			overlap2 = (offset+times2n).reject({ arg item, i; item > (smallestdur+offset) });

			//concatenate both segments from each melody to define the breakpoints for melody1
			envBreakpoints1 = ([offset]++trim1++overlap2).asSet.asArray.sort;


			//locate the breakpoints for the 2nd melody

			//remove breakpoints from second melody outside bounds of overlap
			trim2 = times2n.reject({ arg item, i; item > smallestdur });

			//locate the breakpoints for the 1st melody that correspond to the breakpoints of the 2nd melody
			//remove breakpoints outside the bounds of overlap

			overlap4 = (times1).reject
			(
				{ arg item, i;
					item > smallestdur }
			);

			overlap3 = (overlap4-offset).reject({ arg item, i; item < 0 });

			//concatenate both segments from each melody to define the breakpoints for melody1
			envBreakpoints2 = (trim2++overlap3).asSet.asArray.sort;

		}{

		};

		envBreakpoints1.do({ arg item, i; outlevels1.add(this.melToEnv.at(item)) });
		envBreakpoints2.do({ arg item, i; outlevels2.add(melody2.melToEnv.at(item)) });


		//Melody.new(outlevels1-outlevels2, envBreakpoints2.differentiate).melToEnv;
		outp = envBreakpoints2.differentiate.asArray;
		outp.removeAt(0);

		["smallestdur",smallestdur,times1, times2, envBreakpoints1,envBreakpoints2,envBreakpoints2.differentiate.sum, outp++[smallestdur-(envBreakpoints2.differentiate.sum)], outlevels1-outlevels2].postcs;
		outenv = outlevels1-outlevels2;

		finaltimes = outp++[smallestdur-(envBreakpoints2.differentiate.sum)];
		"finaltimes".postcs;
		finaltimes.postcs;
		"outenv".postcs;
		outenv.postcs;
		outenv[0].postcs;
		finallevels = outenv[0].asArray++outenv.asArray;

		"finallevels".postcs;
		finallevels.postcs;
		//create list of intervals

		outlevels2.do({ arg item, i;

			current_interval = pcoll.distanceFromFilter(PC(item), PC(outlevels1[i]) );

			//current_interval = PC(item).distanceFrom2(PC(outlevels1[i]));

			interval_arr.add(current_interval);
			"current_interval".postcs;
			current_interval.postcs;

			/*
			identify if the harmony is above or below the harmonized melody by tallying positive or negative intervals
			above_interval/below_interval
			Note, distanceFrom2 calculates the difference from the first melody to the second, therefore, if the harmony note is above, the interval to the base melody is down
			*/

			if(current_interval[1].asString == "down".asString)
			{
				above_interval = above_interval+1;
			};

			if(current_interval[1].asString == "up".asString)
			{
				below_interval = below_interval+1;
			};

			/*
			identify if the harmony intervals are acceptable given the specified legal_intervals
			*/

			if(legal_intervals.asArray.includes((current_interval[0]).asInteger)  )
			{
				good_interval=good_interval+1;
				amended_melody_pcs.add(PC(item));
				steplist.add(0);
			}
			{
				bad_interval=bad_interval+1;
				current_amended_step = legal_intervals[legal_intervals.indexIn((current_interval[0]).asInteger)];
				steplist.add(current_amended_step);
				amended_melody_pcs.add( (PC(outlevels1[i]).finalModalTranspose(PC(outlevels1[i]), pcoll, current_amended_step.asInteger))[4]  );
			};


			if((i > 0)){
				"Voice!!!".postcs;
				i.postln;
				current_interval.postcs;
				current_interval[1].asString.postcs;

				if(current_interval[1].asString!=previous_interval[1].asString)
				{
					"a voice crossed!!!!".postcs;
					voice_crossed=1;
				};

			};

			previous_interval = current_interval;

		});

		/*
		If the harmony melody is above the base melody, the value of harmony_above_below == 1, below == -1 or 0 if there are as many notes above as below
		*/

		harmony_above_below = case
		{above_interval>below_interval}{1}
		{above_interval<below_interval}{1.neg}
		{above_interval==below_interval}{0};

		"harmony_above_below".postcs;
		"(above_interval/below_interval)".postcs;
		[above_interval,below_interval].postcs;
		harmony_above_below.postcs;




		// this.compareMelodies(melody2, offset, legal_intervals, pcoll)[10];

		^[analysis[10].size, analysis[10],  outlevels2 ];
	}


	melody_to_midi_file {arg path = "~/Desktop/midifiletest_01.mid";
		var start_times = this.getTimeDistancesFromZero.asArray,
		notes = this.pCollToKeynums,
		m = SimpleMIDIFile( path ); // create empty file
		m.init1( 3, 120, "4/4" );	// init for type 1 (multitrack); 3 tracks, 120bpm, 4/4 measures
		m.timeMode = \seconds;  // change from default to something useful


		notes.do({ |note, i| // add random notes
			m.addNote( note.round(1), 74 + 4.rand, start_times[i], this.durations[i], 127,
				track: 1 )
		});

		m.adjustEndOfTrack;
		m.write; // now play the file in Quicktime, or open with another app
	}



	/*



	~melody_to_midi_file = {arg melody, path = "~/Desktop/midifiletest22.mid";
	var start_times = melody.getTimeDistancesFromZero.asArray,
	notes = melody.pCollToKeynums;

	m = SimpleMIDIFile( path ); // create empty file
	m.init1( 3, 120, "4/4" );	// init for type 1 (multitrack); 3 tracks, 120bpm, 4/4 measures
	m.timeMode = \seconds;  // change from default to something useful

	notes.do({ |note, i| // add random notes
	m.addNote( note.round(1), 64 + 64.rand, start_times[i], melody.durations[i], 127,
	track: 1 )
	});

	m.adjustEndOfTrack;
	m.write; // now play the file in Quicktime, or open with another app



	};
	*/






}
/* To Do:

getAverageNote

ratioConsonantToDissonant


envtoMel

melodyIntervals
MelodyIntervalsEnv

transpose
transposeByIntervalArray

sorts by consonant/dissonant relationship
sortmelodyArr{

}

there is a need for an IntervalCollection...with th same collections as all of the other methods...
is it necessary?

stretch
warp

fractal
powerset

}
*/


Mel : Melody { }
