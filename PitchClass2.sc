/* For GUIDO objects - no quartertones yet */

PitchClass2 {
	var <note, <acc, <octave, <pitch, <pitchclass, <keynum, <freq, <>alter;
	classvar notenames, notenums, noteToScale, scaleToNote, accToSize, sizeToAcc, accToGuido,
	majScale, qualities, qualities2, qualIdx;
	// deal with transposition, notenames and PC classes here
	// note and acc are symbols, octave is an integer, where middle c = c4

	*new {arg pitch, octave = 4, alter = 0;
		^super.new.initPC(pitch, octave, alter);
	}


	initPC {arg argpitch, argoctave, argalter;
		alter = argalter;
		this.calcpitch(argpitch, argoctave);
	}

	calcpitch {arg thispitch, thisoctave;
		var str, pitchnum;
		octave = thisoctave ?? {octave};
		thispitch.isKindOf(Number).if({
			octave = (thispitch.round*0.0833333333).floor - 1;
			pitchnum = thispitch % 12;
			(pitchnum == -0).if({pitchnum = 0});
			(pitchnum == 0).if({octave = octave + 1});
			pitch = notenums[pitchnum];
		}, {
			pitch = thispitch;
		});
		str = pitch.asString;
		note = str[0].asSymbol;
		if(str.size > 1,
			{acc = str[1..str.size-1].asSymbol},
			{pitch = (str ++ "n").asSymbol; acc = \n}
		);
		pitch = pitch.asSymbol;
		pitchclass = notenames[pitch];
		(pitchclass >= 0).if({
			keynum = pitchclass + (12 * (1 + octave));
			freq = keynum.midicps;
		}, {
			keynum = 0;
			freq = 0;
		});
	}

	pitch_ {arg newpitch, newoctave;
		this.calcpitch(newpitch, newoctave);
	}

	guidoString {
		var oct, gacc;
		oct = (octave - 3).asInteger;
		gacc = accToGuido[acc];
		(note == \r).if({
			note = "_";
			oct = "";
		});
		^note.asString++gacc++oct;
	}

	lilyString {
		var oct, octString, lacc;
		oct = octave - 3;
		octString = "";
		lacc = (acc != \n).if({acc.asString}, {""});
		case
		{note == \r} {nil}
		{oct > 0} {oct.do({octString = octString ++ "'"})}
		{oct < 0} {oct.abs.do({octString = octString ++ ","})};		^note.asString++lacc++octString;
	}

	// can be a PitchClass or float that represents a keynum (quartertones are okay this way)
	invert {arg center;
		var change;
		center = center.isKindOf(PitchClass2).if({
			center.keynum
		}, {
			center ?? {60}
		});
		change = this.keynum - center * 2;
		^this.class.new(this.keynum - change)
	}

	//	// direction should be \up or \down - aPitchInterval can be an instance of PitchInterval
	// OR an + or - integer (direction can be exculded in this case
	transpose {arg aPitchInterval, direction = \up;
		var startscale, endnote, numnotes, newscale, newoctave, newacc, size, sizeAlter;
		var intervalSize, modIntervalSize, intervalQuality, dir;
		dir = case
		{direction == \up}{1}
		{direction == \down}{-1}
		// if neither, set direction to up and return 1
		{true}{direction = \up; 1};
		aPitchInterval.isKindOf(PitchInterval2).if({
			intervalSize = aPitchInterval.size;
			modIntervalSize = aPitchInterval.mod;
			intervalQuality = aPitchInterval.quality;
			startscale = noteToScale[note];
			numnotes = intervalSize - 1 * dir;
			newscale = (startscale + numnotes);
			newoctave = (newscale / 7).floor + octave.asFloat;
			endnote = scaleToNote[newscale % 7];
			// distance from the 'natural' note
			size = accToSize[acc];

			// need to work in exceptions for scales!
			sizeAlter =
			case
			{((modIntervalSize == 1) && (intervalQuality == \perf))}
			{0 * dir}
			{((modIntervalSize == 1) && (intervalQuality == \dim))}
			{-1 * dir}
			{((modIntervalSize == 1) && (intervalQuality == \aug))}
			{1 * dir}
			{((modIntervalSize == 2) && (intervalQuality == \dim))}
			{-2 * dir}
			{((modIntervalSize == 2) && (intervalQuality == \minor))}
			{(direction == \up).if({
				case
				{((note == \b) || (note == \e))} {0}
				{true} {-1};
			}, {
				case
				{((note == \c) || (note == \f))} {0}
				{true} {1};
			}
			)}
			{((modIntervalSize == 2) && (intervalQuality == \major))}
			{(direction == \up).if({
				case
				{((note == \b) || (note == \e))} {1}
				{true} {0};
			}, {
				case
				{((note == \c) || (note == \f))} {-1}
				{true} {0};
			}
			)}
			{((modIntervalSize == 2) && (intervalQuality == \aug))}
			{(direction == \up).if({
				case
				{((note == \b) || (note == \e))} {2}
				{true} {1};
			}, {
				case
				{((note == \c) || (note == \f))} {-2}
				{true} {-1};
			}
			)}
			{((modIntervalSize == 3) && (intervalQuality == \dim))}
			{(direction == \up).if({
				case
				{((note == \c) || (note == \f) || (note == \g))} {-2}
				{true} {-1};
			}, {
				case
				{((note == \a) || (note == \b) || (note == \e))} {2}
				{true} {1};
			}
			)}
			{((modIntervalSize == 3) && (intervalQuality == \minor))}
			{(direction == \up).if({
				case
				{((note == \c) || (note == \f) || (note == \g))} {-1}
				{true} {0};
			}, {
				case
				{((note == \a) || (note == \b) || (note == \e))} {1}
				{true} {0};
			}
			)}
			{((modIntervalSize == 3) && (intervalQuality == \major))}
			{(direction == \up).if({
				case
				{((note == \c) || (note == \f) || (note == \g))} {0}
				{true} {1};
			}, {
				case
				{((note == \a) || (note == \b) || (note == \e))} {0}
				{true} {-1};
			}
			)}
			{((modIntervalSize == 3) && (intervalQuality == \aug))}
			{(direction == \up).if({
				case
				{((note == \c) || (note == \f) || (note == \g))} {1}
				{true} {2};
			}, {
				case
				{((note == \a) || (note == \b) || (note == \e))} {-1}
				{true} {-2};
			}
			)}
			{((modIntervalSize == 4) && (intervalQuality == \dim))}
			{(direction == \up).if({
				case
				{(note == \f)} {-2}
				{true} {-1};
			}, {
				case
				{(note == \b)} {2}
				{true} {1};
			}
			)}
			{((modIntervalSize == 4) && (intervalQuality == \perf))}
			{(direction == \up).if({
				case
				{(note == \f)} {-1}
				{true} {0};
			}, {
				case
				{(note == \b)} {1}
				{true} {0};
			}
			)}
			{((modIntervalSize == 4) && (intervalQuality == \aug))}
			{(direction == \up).if({
				case
				{(note == \f)} {-2}
				{true} {-1};
			}, {
				case
				{(note == \b)} {2}
				{true} {1};
			}
			)}
			{((modIntervalSize == 5) && (intervalQuality == \dim))}
			{(direction == \up).if({
				case
				{(note == \b)} {0}
				{true} {-1};
			}, {
				case
				{(note == \f)} {0}
				{true} {1};
			}
			)}
			{((modIntervalSize == 5) && (intervalQuality == \perf))}
			{(direction == \up).if({
				case
				{(note == \b)} {1}
				{true} {0};
			}, {
				case
				{(note == \f)} {-1}
				{true} {0};
			}
			)}
			{((modIntervalSize == 5) && (intervalQuality == \aug))}
			{(direction == \up).if({
				case
				{(note == \b)} {2}
				{true} {1};
			}, {
				case
				{(note == \f)} {-2}
				{true} {-1};
			}
			)}
			{((modIntervalSize == 6) && (intervalQuality == \dim))}
			{(direction == \up).if({
				case
				{((note == \e) || (note == \a) || (note == \b))} {-1}
				{true} {-2};
			}, {
				case
				{((note == \c) || (note == \f) || (note == \g))} {1}
				{true} {2};
			}
			)}
			{((modIntervalSize == 6) && (intervalQuality == \minor))}
			{(direction == \up).if({
				case
				{((note == \e) || (note == \a) || (note == \b))} {0}
				{true} {-1};
			}, {
				case
				{((note == \c) || (note == \f) || (note == \g))} {0}
				{true} {1};
			}
			)}
			{((modIntervalSize == 6) && (intervalQuality == \major))}
			{(direction == \up).if({
				case
				{((note == \e) || (note == \a) || (note == \b))} {1}
				{true} {0};
			}, {
				case
				{((note == \c) || (note == \f) || (note == \g))} {-1}
				{true} {0};
			}
			)}
			{((modIntervalSize == 6) && (intervalQuality == \aug))}
			{(direction == \up).if({
				case
				{((note == \e) || (note == \a) || (note == \b))} {2}
				{true} {1};
			}, {
				case
				{((note == \c) || (note == \f) || (note == \g))} {-2}
				{true} {-1};
			}
			)}
			{((modIntervalSize == 7) && (intervalQuality == \dim))}
			{(direction == \up).if({
				case
				{((note == \c) || (note == \f))} {-2}
				{true} {-1};
			}, {
				case
				{((note == \b) || (note == \e))} {2}
				{true} {1};
			}
			)}
			{((modIntervalSize == 7) && (intervalQuality == \minor))}
			{(direction == \up).if({
				case
				{((note == \c) || (note == \f))} {-1}
				{true} {0};
			}, {
				case
				{((note == \b) || (note == \e))} {1}
				{true} {0};
			}
			)}
			{((modIntervalSize == 7) && (intervalQuality == \major))}
			{(direction == \up).if({
				case
				{((note == \c) || (note == \f))} {0}
				{true} {1};
			}, {
				case
				{((note == \b) || (note == \e))} {0}
				{true} {-1};
			}
			)}
			{((modIntervalSize == 7) && (intervalQuality == \aug))}
			{(direction == \up).if({
				case
				{((note == \c) || (note == \f))} {1}
				{true} {2};
			}, {
				case
				{((note == \b) || (note == \e))} {-1}
				{true} {-2};
			}
			)}
			{true} {0};
			newacc = sizeToAcc[size + sizeAlter];
			^this.class.new((endnote ++ newacc).asSymbol, newoctave);
		}, {
			^this.class.new(this.keynum + (aPitchInterval * dir))
		})
	}

	//get PitchInterval between two PitchClasses
	getInterval {arg aPC2;
		var direction, steps = aPC2.keynum - this.keynum, octave, base_interval, p_interval,

		//i could make this an array or linkedlist

		interval_list = List[
			[\unison,1],
			[\minor,2],
			[\major,2],
			[\minor,3],
			[\major,3],
			[\perf,4],
			[\dim,5],
			[\perf,5],
			[\minor,6],
			[\major,6],
			[\minor,7],
			[\major,7]
		];

		if((this.class.asString!="PC2".asString)|| (aPC2.class.asString!="PC".asString)){
			//"ARGUMENTS NOT OF TYPE PITCH CLASS"
		}{
			(this.keynum < aPC2.keynum).if({
				direction = \up;
			},
			{
				if((this.keynum == aPC2.keynum)){
					direction = \unison;
				}{
					direction = \down;
				};
			});
		};

		base_interval = interval_list[(steps.abs.asInteger%12)];
		octave = ((steps.abs.asInteger/12).trunc)*7;
		p_interval = PitchInterval2.new(base_interval[0],base_interval[1] + octave);
		^[steps,direction, base_interval, base_interval + octave, p_interval];
	}

	stepsToInterval{arg halfsteps;
		if(halfsteps.isPositive){
			^PC2(69).getInterval(PC2(69+halfsteps))[3][1];
		}{
			^PC2(69).getInterval(PC2(69-halfsteps))[3][1];
		};
	}

	distanceFrom {arg aPC;
		var thisNote, thatNote, baseInterval, interval, direction, octaves;
		var halfsteps, idx, quality;
		thisNote = noteToScale[note];
		thatNote = noteToScale[aPC.note];
		(aPC.keynum > keynum).if({
			direction = \up;
			(thatNote < thisNote).if({
				thatNote = thatNote + 7;
			})
		}, {
			direction = \down;
			(thatNote > thisNote).if({
				thatNote = thatNote - 7;
			})
		});
		baseInterval = (thatNote - thisNote);
		halfsteps = (aPC.keynum - keynum).abs;
		interval = baseInterval.abs + 1;
		octaves = (halfsteps * 0.083333333333333).floor;
		idx = (halfsteps % 12) - qualIdx[interval];
		quality = qualities[interval][idx];
		^[PitchInterval2(quality, interval + (octaves * 7)), direction];
	}

	modalharmonychoices {arg aPC, toAPitchCollection, scale, steps;
		var a, b, al=LinkedList.new, bl=LinkedList.new, interlist=[2,3,4,5], out = LinkedList.new,  outd = LinkedList.new, outu = LinkedList.new;
		a= aPC;
		b = aPC.mtrans1(aPC, toAPitchCollection, scale, steps);
		interlist.do({ arg item, i;  al.add(aPC.mtrans1(aPC, toAPitchCollection, scale, item)); });
		interlist.do({ arg item, i;  bl.add(b.mtrans1(b, toAPitchCollection, scale, item)); });
		([al.asArray, bl.asArray].allTuples).do({ arg item, i;

			out.add([[item[0].keynum, item[1].keynum],item[0], item[1], item[0].distanceFrom(item[1])]);
			//out.add(item);

		});
		out.postcs;

		out.do({ arg item, i;

			//	outd.add();
			if((item[3][1])=='down'){
				outd.add(item)
			}{
				outu.add(item)

			};


		});

		outd.postcs;
		"outu".postcs;

		outu.postcs;
		"outu-end".postcs;

		//^[al, bl, [al.asArray, bl.asArray].allTuples.size, "([al.asArray, bl.asArray].allTuples)",out];
		^[outu, outd];

	}

	mharmc1 {arg aPC, toAPitchCollection, scale, aPC2;
		var a, b, al=LinkedList.new, bl=LinkedList.new, interlist=[2,3,4,5,7,9], out = LinkedList.new,  outd = LinkedList.new, outu = LinkedList.new;
		a= aPC;
		b = aPC2;
		interlist.do({ arg item, i;  al.add(aPC.mtrans1(aPC, toAPitchCollection, scale, item)); });
		interlist.do({ arg item, i;  bl.add(b.mtrans1(b, toAPitchCollection, scale, item)); });
		([al.asArray, bl.asArray].allTuples).do({ arg item, i;

			out.add([[item[0].keynum, item[1].keynum],item[0], item[1], item[0].distanceFrom(item[1])]);
			//out.add(item);

		});
		//out.postcs;

		out.do({ arg item, i;

			//	outd.add();
			if((item[3][1])=='down'){
				outd.add(item)
			}{
				outu.add(item)

			};


		});

		//outd.postcs;
		//"outu".postcs;

		//outu.postcs;
		//"outu-end".postcs;

		//^[al, bl, [al.asArray, bl.asArray].allTuples.size, "([al.asArray, bl.asArray].allTuples)",out];
		^[outu, outd];

	}


	/*
	gives a scale degree in the form of zero based counting:
	*/

	getScaleDegree {arg aPC, toAPitchCollection;
		var thisNote, pc_degrees, scaledeg, notediff, degdiffs, degdiffsrev, notedeg, stepstoscaledeg, thatNote, basenote, basenote1, interval, direction, octaves, outnote=0, outnote2, above = 1, normalform, scaledegree;
		basenote = toAPitchCollection.tonic.pitch;
		basenote1 = aPC.pitch;
		pc_degrees = toAPitchCollection.degrees;
		notediff = aPC.keynum-toAPitchCollection.tonic.keynum;

		if(notediff.isNegative)
		{
			above=1.neg;
		};

		//get the step number for the note closest to the tonic

		normalform = (notediff.abs % 12)*above;

		//find scale degree of the PC

		if(above==1){
			scaledegree =	pc_degrees.indexOf(normalform.asInteger);
		}{
			scaledegree = ((pc_degrees-12).replace(-12,0)).indexOf(normalform.asInteger);
		};

		if(scaledegree==nil){

			"is not in the scale".postcs;
		};
/*
		[basenote1,basenote, notediff,pc_degrees, above, "normalform", normalform, pc_degrees,scaledegree].postcs;
*/
		scaledeg= Dictionary.new;
		stepstoscaledeg= Dictionary.new;
		degdiffs= Dictionary.new;
		degdiffsrev= Dictionary.new;

		^scaledegree.asInteger;
	}

	normalForm {arg aPC, toAPitchCollection;
		var thisNote, pc_degrees, scaledeg, notediff, degdiffs, degdiffsrev, notedeg, stepstoscaledeg, thatNote, basenote, basenote1, interval, direction, octaves, outnote=0, outnote2, above = 1, normalform, scaledegree;
		basenote = toAPitchCollection.tonic.pitch;
		basenote1 = aPC.pitch;
		pc_degrees = toAPitchCollection.degrees;
		notediff = aPC.keynum-toAPitchCollection.tonic.keynum;

		if(notediff.isNegative)
		{
			above=1.neg;
		};

		//get the step number for the note closest to the tonic

		normalform = (notediff.abs % 12)*above;

		//find scale degree of the PC

		if(above==1){
			scaledegree =	pc_degrees.indexOf(normalform.asInteger);
		}{
			scaledegree = ((pc_degrees-12).replace(-12,0)).indexOf(normalform.asInteger);
		};

		if(scaledegree==nil){

			"is not in the scale".postcs;
		};
/*
		[basenote1,basenote, notediff,pc_degrees, above, "normalform", normalform, pc_degrees,scaledegree].postcs;
*/
		scaledeg= Dictionary.new;
		stepstoscaledeg= Dictionary.new;
		degdiffs= Dictionary.new;
		degdiffsrev= Dictionary.new;

		^normalform.asInteger;
	}




	get_three_part_voicings {arg voice = 0, firstlast_or_middle = 0, pc = PColl2.lydian(\a);
		var temp_val, outlist ,
		voicing_table_first_last = [
			[2,2],[2,5], [2,7], [2,9],
			[3, 4],
			[4, 7],
			[5,2], [5, 4], [5,9],
			[7, 2], [7, 4], [7, 9]
		],
		/*
		extra voicings for allowing for first inversion in the middle of a harmonization
		*/
		voicing_table_middle = [
			[3,2], [3,9],
			[4,5],
			[5,7],
			[7,5]];

		if(firstlast_or_middle == 0){
			temp_val = voicing_table_first_last.choose;

		}{
			voicing_table_first_last = voicing_table_first_last++voicing_table_middle;
			temp_val = voicing_table_first_last.choose;
		};

		case
		{voice == 0}{
			outlist = [this.keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0].neg)[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.sum.neg)[4].keynum

			];
		}
		{voice == 1}{
			outlist = [
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0])[4].keynum,
				this.keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[1].neg)[4].keynum
			];
		}
		{voice == 2}{
			outlist = [
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0])[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.sum)[4].keynum,
				this.keynum
			];
		};

		^outlist;
	}

	get_three_part_voicingsPC {arg voice = 0, firstlast_or_middle = 0, pc = PColl2.lydian(\a);
		var temp_val, outlist ,
		voicing_table_first_last = [
			[2,2],[2,5], [2,7], [2,9],
			[3, 4],
			[4, 7],
			[5,2], [5, 4], [5,9],
			[7, 2], [7, 4], [7, 9]
		],
		/*
		extra voicings for allowing for first inversion in the middle of a harmonization
		*/
		voicing_table_middle = [
			[3,2], [3,9],
			[4,5],
			[5,7],
			[7,5]];

		/*
		create extra voicings for allowing for second inversion in the middle of a harmonization
		*/

		if(firstlast_or_middle == 0){
			temp_val = voicing_table_first_last.choose;

		}{
			voicing_table_first_last = voicing_table_first_last++voicing_table_middle;
			temp_val = voicing_table_first_last.choose;
		};

		case
		{voice == 0}{
			outlist = [this, this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0].neg)[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.sum.neg)[4]];
		}
		{voice == 1}{
			outlist = [this, this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0])[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[1].neg)[4]];
		}
		{voice == 2}{
			outlist = [this, this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0])[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.sum)[4]];
		};

		^outlist;
	}

	get_two_part_voicingsPC_and_Keynum {arg voice = 0, firstlast_or_middle = 0, pc = PColl2.lydian(\a);
		var temp_val, outlist, keynumlist,
		voicing_table_first_last = [0,4,7],voicing_table_last = [0,7], out_table, voicing_table_first = [0,4,7],
		/*
		extra voicings for allowing for first inversion in the middle of a harmonization
		*/
		voicing_table_middle = [2,4,5,7,9];

		/*
		create extra voicings for allowing for second inversion in the middle of a harmonization
		*/
		/*
		if(firstlast_or_middle == 0){
		temp_val = voicing_table_first_last.choose.postcs;

		}{
		voicing_table_first_last = voicing_table_first_last++voicing_table_middle;
		temp_val = voicing_table_first_last.choose;
		};
		*/
		case
		{firstlast_or_middle == 0}{
			temp_val = voicing_table_first.choose.postcs;
		}
		{firstlast_or_middle == 1}{
			//voicing_table_first_last = voicing_table_middle;
			temp_val = voicing_table_middle.choose;
		}
		{firstlast_or_middle == 2}{
			//voicing_table_first_last = voicing_table_first_last++voicing_table_middle;
			temp_val = voicing_table_last.choose;
		}{
			temp_val = voicing_table_middle.choose;

		};


		case
		{voice == 0}{
			outlist = [
				this,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.neg)[4]
			];

			keynumlist = [
				this.keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.neg)[4].keynum,
			];

		}
		{voice == 1}{
			outlist = [

				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val)[4],
				this
			];

			keynumlist = [

				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val)[4].keynum,
				this.keynum
			];
		}

		^[outlist, keynumlist];
	}

	get_three_part_voicingsPC_and_Keynum {arg voice = 0, firstlast_or_middle = 0, pc = PColl2.lydian(\a);
		var temp_val, outlist, keynumlist,
		voicing_table_first_last = [
			[2,2],[2,5], [2,7], [2,9],
			[3, 4],
			[4, 7],
			[5,2], [5, 4], [5,9],
			[7, 2], [7, 4], [7, 9]
		],
		/*
		extra voicings for allowing for first inversion in the middle of a harmonization
		*/
		voicing_table_middle = [
			[3,2], [3,9],
			[4,5],
			[5,7],
			[7,5]];

		/*
		create extra voicings for allowing for second inversion in the middle of a harmonization
		*/

		if(firstlast_or_middle == 0){
			temp_val = voicing_table_first_last.choose;

		}{
			voicing_table_first_last = voicing_table_first_last++voicing_table_middle;
			temp_val = voicing_table_first_last.choose;
		};

		case
		{voice == 0}{
			outlist = [
				this,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0].neg)[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.sum.neg)[4]
			];

			keynumlist = [
				this.keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0].neg)[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.sum.neg)[4].keynum
			];

		}
		{voice == 1}{
			outlist = [
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0])[4],
				this,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[1].neg)[4]
			];

			keynumlist = [
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0])[4].keynum,
				this.keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[1].neg)[4].keynum
			];
		}
		{voice == 2}{
			outlist = [
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0])[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.sum)[4],
				this
			];

			keynumlist = [
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0])[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.sum)[4].keynum,
				this.keynum
			];
		};

		^[outlist, keynumlist];
	}
	//non harmonic tone voicings: [1,6]
	/*
	if im going to do a non harmonic tone voicing, look to the previous voicing table and find the smallest difference to choose

	*/

	get_two_part_voicingsPC_and_KeynumTable {arg voice = 0, firstlast_or_middle = 0, pc = PColl2.lydian(\a);
		var temp_val, outlist = LinkedList.new, keynumlist = LinkedList.new, out_table, voicing_table_first = [0,4,7], tkeynumlist = LinkedList.new,
		voicing_table_first_last = [0,4,7],voicing_table_last = [0,7], new_voice_table_set = [[0,4,7],[2,4,5,7,9],[0,7]], maxtablesize, maxtableindex;

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

		//"maxtableindex".postcs;
		//maxtableindex.postcs;

		//get the table to iterate
		if(firstlast_or_middle >= new_voice_table_set.size){

			out_table = new_voice_table_set[firstlast_or_middle];

			temp_val = new_voice_table_set[maxtableindex];
		}{
			out_table = new_voice_table_set[firstlast_or_middle];

			temp_val = new_voice_table_set[firstlast_or_middle];
		};

		//"temp_val".postcs;
		//temp_val.postcs;

		temp_val.do({arg table_element, oi;


			if(table_element.isArray == false){

				if(voice == 0){
					tkeynumlist.add([
						this.keynum,
						this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element.neg)[4].keynum
					]);
				}{
					tkeynumlist.add([
						this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element)[4].keynum,
						this.keynum
					]);
				};

			}{


			};


			case
			{voice == 0}{
				outlist.add([
					this,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element.neg)[4]
				]);

				keynumlist.add([
					this.keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element.neg)[4].keynum,
				]);

			}
			{voice == 1}{
				outlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element)[4],
					this
				]);

				keynumlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element)[4].keynum,
					this.keynum
				]);
			};

		});
		//"tkeynumlist".postcs;

		//tkeynumlist.postcs;
		//"tkeynumlist".postcs;

		^[outlist, keynumlist];
	}




	get_three_part_voicingsPC_and_KeynumTable {arg voice = 0, firstlast_or_middle = 0, pc = PColl2.lydian(\a);
		var temp_val, outlist = LinkedList.new, keynumlist = LinkedList.new, out_table, voicing_table_first, voicing_table_last,
		voicing_table_first_last = [
			[2,2],[2,5], [2,7], [2,9],
			[3, 4],
			[4, 7],
			[5,2], [5, 4], [5,9],
			[7, 2], [7, 4], [7, 9]
		],
		/*
		extra voicings for allowing for first inversion in the middle of a harmonization
		*/
		voicing_table_middle = [
			[3,2], [3,9],
			[4,5],
			[5,7],
			[7,5]];

		/*
		create extra voicings for allowing for second inversion in the middle of a harmonization
		*/

		if(firstlast_or_middle == 0){
			temp_val = voicing_table_first_last;

		}{
			voicing_table_first_last = voicing_table_first_last++voicing_table_middle;
			temp_val = voicing_table_first_last;
		};

		temp_val.do({arg table_element, oi;

			case
			{voice == 0}{
				outlist.add([
					this,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0].neg)[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element.sum.neg)[4]
				]);

				keynumlist.add([
					this.keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0].neg)[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element.sum.neg)[4].keynum
				]);

			}
			{voice == 1}{
				outlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0])[4],
					this,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[1].neg)[4]
				]);

				keynumlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0])[4].keynum,
					this.keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[1].neg)[4].keynum
				]);
			}
			{voice == 2}{
				outlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0])[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element.sum)[4],
					this
				]);

				keynumlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0])[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element.sum)[4].keynum,
					this.keynum
				]);
			};
		});

		^[outlist, keynumlist];
	}



	get_four_part_voicingsPC_and_Keynum {arg voice = 0, firstlast_or_middle = 0, pc = PColl2.lydian(\a);
		var temp_val, outlist, keynumlist,
		voicing_table_first_last = [
			[2,2,7],[2,3,4], [2,5,2], [2,5,9], [2,7,2], [2,7,9],
			[3,2,2],[3,2,9],
			[4,5,2],[4,5,9],
			[5,2,2],[5,2,9],[5,4,2],[5,4,7],[5,5,4],
			[7,2,7],[7,5,4]
		],
		/*
		extra voicings for allowing for first inversion in the middle of a harmonization
		*/
		voicing_table_middle = [
			[2,2,5],[2,3,9], [2,7,5],
			[3,2,7],[3,4,5],
			[4,5,7],[4,7,5],
			[5,2,5],[5,4,5],[5,5,2],[5,5,9],
			[7,3,2],[7,3,9], [7,5,7]
		];

		/*
		create extra voicings for allowing for second inversion in the middle of a harmonization
		*/

		if(firstlast_or_middle == 0){
			temp_val = voicing_table_first_last.choose.postcs;

		}{
			voicing_table_first_last = voicing_table_first_last++voicing_table_middle;
			temp_val = voicing_table_first_last.choose;
		};

		case
		{voice == 0}{//all voices below cantus
			outlist = [
				this,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0].neg)[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (temp_val[0] + temp_val[1]).neg)[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.sum.neg)[4]
			].postcs;

			keynumlist = [
				this.keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0].neg)[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (temp_val[0] + temp_val[1]).neg)[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.sum.neg)[4].keynum].postcs;

		}
		{voice == 1}{//1 voice above cantus 2 below
			outlist = [
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0])[4],
				this,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[1].neg)[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (temp_val[1] + temp_val[2]).neg)[4]].postcs;

			keynumlist = [
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0])[4].keynum,
				this.keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[1].neg)[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (temp_val[1] + temp_val[2]).neg)[4].keynum].postcs;
		}
		{voice == 2}{//2 voice above cantus 1 below
			outlist = [
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0] + temp_val[1])[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[1])[4],
				this,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[2].neg)[4]].postcs;

			keynumlist = [
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0] + temp_val[1])[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[1])[4].keynum,
				this.keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[2].neg)[4].keynum].postcs;
		}
		{voice == 3}{
			outlist = [
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.sum)[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0] + temp_val[1])[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0])[4],
				this
			].postcs;

			keynumlist = [
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.sum)[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0] + temp_val[1])[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0])[4].keynum,
				this.keynum
			].postcs;
		};

		^[outlist, keynumlist];
	}

	/*
	get whole table of voicings
	*/

	get_four_part_voicingsPC_and_KeynumTable {arg voice = 0, firstlast_or_middle = 0, pc = PColl2.lydian(\a);
		var temp_val, outlist = LinkedList.new, keynumlist = LinkedList.new,
		voicing_table_first_last = [
			[2,2,7],[2,3,4], [2,5,2], [2,5,9], [2,7,2], [2,7,9],
			[3,2,2],[3,2,9],
			[4,5,2],[4,5,9],
			[5,2,2],[5,2,9],[5,4,2],[5,4,7],[5,5,4],
			[7,2,7],[7,5,4]
		],
		/*
		extra voicings for allowing for first inversion in the middle of a harmonization
		*/
		voicing_table_middle = [
			[2,2,5],[2,3,9], [2,7,5],
			[3,2,7],[3,4,5],
			[4,5,7],[4,7,5],
			[5,2,5],[5,4,5],[5,5,2],[5,5,9],
			[7,3,2],[7,3,9], [7,5,7]
		];

		/*
		create extra voicings for allowing for second inversion in the middle of a harmonization
		*/


		if(firstlast_or_middle == 0){
			temp_val = voicing_table_first_last;

		}{
			voicing_table_first_last = voicing_table_first_last++voicing_table_middle;
			temp_val = voicing_table_first_last;
		};

		temp_val.do({arg table_element, oi;

			case
			{voice == 0}{//all voices below cantus
				outlist.add([
					this,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0].neg)[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (table_element[0] + table_element[1]).neg)[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element.sum.neg)[4]
				]);

				keynumlist.add([
					this.keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0].neg)[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (table_element[0] + table_element[1]).neg)[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element.sum.neg)[4].keynum]);

			}
			{voice == 1}{//1 voice above cantus 2 below
				outlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0])[4],
					this,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[1].neg)[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (table_element[1] + table_element[2]).neg)[4]]);

				keynumlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0])[4].keynum,
					this.keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[1].neg)[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (table_element[1] + table_element[2]).neg)[4].keynum]);
			}
			{voice == 2}{//2 voice above cantus 1 below
				outlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0] + table_element[1])[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[1])[4],
					this,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[2].neg)[4]]);

				keynumlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0] + table_element[1])[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[1])[4].keynum,
					this.keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[2].neg)[4].keynum]);
			}
			{voice == 3}{
				outlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element.sum)[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0] + table_element[1])[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0])[4],
					this
				]);

				keynumlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element.sum)[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0] + table_element[1])[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0])[4].keynum,
					this.keynum
				]);
			};
		});

		^[outlist, keynumlist];
	}

	get_five_part_voicingsPC_and_Keynum {arg voice = 0, firstlast_or_middle = 0, pc = PColl2.lydian(\a);
		var temp_val, outlist = LinkedList.new, keynumlist = LinkedList.new, tempindex,
		voicing_table_first_last = [
			[2,2,2,2], [2,2,2,7], [2,3,3,3], [2,3,4,2], [2,5,3,6], [2,5,5,6], [2,7,3,6],
			[3,2,2,2],[3,2,4,2], [3,2,2,9],[3,3,3,2],[3,3,3,3],[3,4,5,4],
			[4,2,3,4],[4,3,2,7],[4,5,3,6],[4,5,4,7],
			[5,2,2,7],[5,4,2,4],[5,4,2,7],[5,4,7,2],[5,6,5,2],
			[7,2,2,2],[7,3,2,4]
		],
		/*
		extra voicings for allowing for first inversion in the middle of a harmonization
		*/
		voicing_table_middle = [
			[2,2,5,3],[2,3,4,9], [2,7,2,7],
			[3,2,7,2],[3,4,5,7],
			[4,5,3,4],[4,5,3,6],[4,7,3,2],[4,7,5,5],
			[5,2,2,2],[5,2,2,7],[5,4,5,4],[5,5,2,2],[5,5,2,5],
			[7,2,2,9],[7,2,4,5],[7,3,2,7],[7,4,5,4]
		];

		/*
		create extra voicings for allowing for second inversion in the middle of a harmonization
		*/


		if(firstlast_or_middle == 0){
			temp_val = voicing_table_first_last;

		}{
			voicing_table_first_last = voicing_table_first_last++voicing_table_middle;
			temp_val = voicing_table_first_last.choose;
		};

		//tempindex = Array.series(temp_val.size,0,1).choose;



		case
		{voice == 0}{//all voices below cantus
			outlist.add([
				this,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0].neg)[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (temp_val[0] + temp_val[1]).neg)[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (temp_val[0] + temp_val[1] + temp_val[2]).neg)[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.sum.neg)[4]
			]);

			keynumlist.add([
				this.keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0].neg)[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (temp_val[0] + temp_val[1]).neg)[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (temp_val[0] + temp_val[1] + temp_val[2]).neg)[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.sum.neg)[4].keynum]);

		}
		{voice == 1}{//1 voice above cantus 2 below
			outlist.add([
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0])[4],
				this,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[1].neg)[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (temp_val[1] + temp_val[2]).neg)[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (temp_val[1] + temp_val[2] + temp_val[3]).neg)[4],

			]);

			keynumlist.add([
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0])[4].keynum,
				this.keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[1].neg)[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (temp_val[1] + temp_val[2]).neg)[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (temp_val[1] + temp_val[2] + temp_val[3]).neg)[4].keynum,

			]);
		}
		{voice == 2}{//2 voice above cantus 1 below
			outlist.add([
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0] + temp_val[1])[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[1])[4],
				this,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[2].neg)[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (temp_val[2] + temp_val[3]).neg)[4]
			]);

			keynumlist.add([
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0] + temp_val[1])[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[1])[4].keynum,
				this.keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[2].neg)[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (temp_val[2] + temp_val[3]).neg)[4].keynum
			]);
		}
		{voice == 3}{
			outlist.add([
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0] + temp_val[1] + temp_val[2])[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[1] + temp_val[2])[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[2])[4],
				this,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[3].neg)[4]
			]);

			keynumlist.add([
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[0] + temp_val[1] + temp_val[2])[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[1] + temp_val[2])[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[2])[4].keynum,
				this.keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[3].neg)[4].keynum
			]);
		}
		{voice == 4}{
			outlist.add([
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.sum)[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[1] + temp_val[2] + temp_val[3])[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[2] + temp_val[3])[4],
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[3].neg)[4],
				this
			]);

			keynumlist.add([
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val.sum)[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[1] + temp_val[2] + temp_val[3])[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[2] + temp_val[3])[4].keynum,
				this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, temp_val[3])[4].keynum,
				this.keynum
			]);
		};


		^[outlist.asArray[0], keynumlist.asArray[0]];
	}

	get_five_part_voicingsPC_and_KeynumTable {arg voice = 0, firstlast_or_middle = 0, pc = PColl2.lydian(\a);
		var temp_val, outlist = LinkedList.new, keynumlist = LinkedList.new,
		voicing_table_first_last = [
			[2,2,2,2], [2,2,2,7],[2,2,4,5], [2,3,3,3], [2,3,4,2], [2,5,3,6], [2,5,5,6], [2,7,3,6],
			[3,2,2,2],[3,2,4,2], [3,2,2,9],[3,2,3,5],[3,3,3,2],[3,3,3,3],[3,4,5,4],
			[4,2,3,4],[4,3,2,7],[4,3,3,6],[4,4,4,4],[4,4,2,6],[4,2,3,6],[4,5,3,6],[4,5,4,7],
			[5,2,2,7],[5,4,2,4],[5,4,2,7],[5,4,7,2],[5,6,5,2],
			[7,2,2,2],[7,3,2,4]
		],
		/*
		extra voicings for allowing for first inversion in the middle of a harmonization
		*/
		voicing_table_middle = [
			[2,2,5,3],[2,3,4,9], [2,7,2,7],
			[3,2,7,2],[3,4,5,7],
			[4,5,3,4],[4,5,3,6],[4,7,3,2],[4,7,5,5],
			[5,2,2,2],[5,2,2,7],[5,4,5,4],[5,5,2,2],[5,5,2,5],
			[7,2,2,9],[7,2,4,5],[7,3,2,7],[7,4,5,4]
		];

		/*
		create extra voicings for allowing for second inversion in the middle of a harmonization
		*/


		if(firstlast_or_middle == 0){
			temp_val = voicing_table_first_last;

		}{
			voicing_table_first_last = voicing_table_first_last++voicing_table_middle;
			temp_val = voicing_table_first_last;
		};

		temp_val.do({arg table_element, oi;

			case
			{voice == 0}{//all voices below cantus
				outlist.add([
					this,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0].neg)[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (table_element[0] + table_element[1]).neg)[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (table_element[0] + table_element[1] + table_element[2]).neg)[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element.sum.neg)[4]
				]);

				keynumlist.add([
					this.keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0].neg)[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (table_element[0] + table_element[1]).neg)[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (table_element[0] + table_element[1] + table_element[2]).neg)[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element.sum.neg)[4].keynum]);

			}
			{voice == 1}{//1 voice above cantus 2 below
				outlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0])[4],
					this,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[1].neg)[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (table_element[1] + table_element[2]).neg)[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (table_element[1] + table_element[2] + table_element[3]).neg)[4],

				]);

				keynumlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0])[4].keynum,
					this.keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[1].neg)[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (table_element[1] + table_element[2]).neg)[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (table_element[1] + table_element[2] + table_element[3]).neg)[4].keynum,

				]);
			}
			{voice == 2}{//2 voice above cantus 1 below
				outlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0] + table_element[1])[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[1])[4],
					this,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[2].neg)[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (table_element[2] + table_element[3]).neg)[4]
				]);

				keynumlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0] + table_element[1])[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[1])[4].keynum,
					this.keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[2].neg)[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, (table_element[2] + table_element[3]).neg)[4].keynum
				]);
			}
			{voice == 3}{
				outlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0] + table_element[1] + table_element[2])[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[1] + table_element[2])[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[2])[4],
					this,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[3].neg)[4]
				]);

				keynumlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[0] + table_element[1] + table_element[2])[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[1] + table_element[2])[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[2])[4].keynum,
					this.keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[3].neg)[4].keynum
				]);
			}
			{voice == 4}{
				outlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element.sum)[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[1] + table_element[2] + table_element[3])[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[2] + table_element[3])[4],
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[3].neg)[4],
					this
				]);

				keynumlist.add([
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element.sum)[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[1] + table_element[2] + table_element[3])[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[2] + table_element[3])[4].keynum,
					this.finalModalTranspose(pc.filterKeynum(this.keynum), pc, table_element[3].neg)[4].keynum,
					this.keynum
				]);
			};
		});

		^[outlist, keynumlist];
	}


	/*
	things to test for: does this work for pentatonic scales? YES!!! CHORDS ETC
	What do I do about notes that arent in the key?
	where do I do the bookkeeping to not throw an error when notes arent int the PC
	Make a couple of methods like is note in scale? nearest note in PitchCOllection
	*/

	finalModalTranspose {arg aPC, toAPitchCollection, steps;
		var scale_degree=aPC.getScaleDegree(aPC, toAPitchCollection), steparr, degree_steps_to_half_steps = 0;

		steparr = (toAPitchCollection.degrees.differentiate++(12-toAPitchCollection.degrees.last));
		steparr.removeAt(0);
		if(steps.isPositive){
			steps.do({ arg item, i;
				degree_steps_to_half_steps = degree_steps_to_half_steps + steparr[((scale_degree+i) % 7)];
				//i.postcs;
			});
		};

		if(steps.isNegative){
			//	"NEGATIVE".postcs;
			steps.abs.do({ arg item, i;

				((scale_degree-i).wrap(0,(steparr.size-1))); //.postcs;
				degree_steps_to_half_steps = degree_steps_to_half_steps+steparr[((scale_degree-i-1).wrap(0,(steparr.size-1)))];
			});
			degree_steps_to_half_steps = degree_steps_to_half_steps.neg;
		};

		^[toAPitchCollection.degrees, steparr, scale_degree, degree_steps_to_half_steps, PC2(degree_steps_to_half_steps+aPC.keynum)];
	}


	mtrans1 {arg aPC, toAPitchCollection, scale, steps;
		var thisNote, scaledeg, notediff, degdiffs, degdiffsrev, notedeg, stepstoscaledeg, thatNote, basenote, basenote1, interval, direction, octaves, outnote=0, outnote2;
		basenote = toAPitchCollection.tonic.pitch;
		basenote1 = aPC.pitch;
		scaledeg= Dictionary.new;
		stepstoscaledeg= Dictionary.new;
		degdiffs= Dictionary.new;
		degdiffsrev= Dictionary.new;



		(scale.differentiate).do({ arg item, i;
			if(item==0){degdiffs.put(i, 1); [i, 1]}{
				degdiffs.put(i, item); [i, item] };
		});

		"end of degrees to steps".postln;

		(scale.differentiate.reverse).do({ arg item, i;
			if(item==0){degdiffsrev.put(i, 1); [i, 1]}{
				degdiffsrev.put(i, item); [i, item] };
		});
		"end of degrees to steps rev".postln;

		scale.do({ arg item, i;  stepstoscaledeg.put(item, i); scaledeg.put(i, item); [i, item] });
		notediff = toAPitchCollection.tonic.keynum-aPC.keynum;


		//the note degree in the scale for the note to be transposed

		if(steps.isNegative){
			notedeg =(stepstoscaledeg[(notediff).abs%12]);
			notedeg.postln;

			//outnote is the number of steps from aPC that should be added to calc the transposed PC
			"calc descending steps".postln;

			(steps.abs).do({ arg item, i; outnote = (outnote)+ degdiffs[(notedeg-i)%degdiffs.size]; ["step index", notedeg-i %degdiffs.size, degdiffs[(notedeg-i)%degdiffs.size]] });
			outnote2 = PC2(aPC.keynum-outnote);


		}{

			notedeg =(stepstoscaledeg[(notediff).abs%12]);
			notedeg.postln;
			//outnote is the number of steps from aPC that should be added to calc the transposed PC
			"calc ascending steps".postln;
			steps.do({ arg item, i; outnote = (outnote)+ degdiffs[((notedeg+1)+i)%degdiffs.size]; ["step index", notedeg+i %degdiffs.size, degdiffs[(notedeg+i)%degdiffs.size]] });
			outnote2 = PC2(aPC.keynum+outnote);
			/*
			I need to get the keynum by adding the steps together..then find the corresponding scale degree and pitch name

			*/

		}




		^outnote2;

		/*
		[notenames[basenote], notenames[basenote1], toAPitchCollection.tonic.distanceFrom(aPC)[0], scale, (toAPitchCollection.tonic).distanceFrom(aPC)[0].mod, (toAPitchCollection.tonic.keynum-aPC.keynum).abs%12, scaledeg, stepstoscaledeg,( stepstoscaledeg[(toAPitchCollection.tonic.keynum-aPC.keynum).abs%12]), "notedeg", notedeg, notedeg +steps, scaledeg[notedeg +steps % scaledeg.size], "outnote", outnote

		];
		*/
		/*
		traverse the degree array by the number of steps from the o position.

		*/

	}




	bimtrans1 {arg aPC, toAPitchCollection, scale, steps;
		var thisNote, scaledeg, notediff, degdiffs, degdiffsrev, notedeg, stepstoscaledeg, thatNote, basenote, basenote1, interval, direction, octaves, outnote=0, outnote2;
		basenote = toAPitchCollection.tonic.pitch;
		basenote1 = aPC.pitch;
		scaledeg= Dictionary.new;
		stepstoscaledeg= Dictionary.new;
		degdiffs= Dictionary.new;
		degdiffsrev= Dictionary.new;



		(scale.differentiate).do({ arg item, i;
			if(item==0){degdiffs.put(i, 1); [i, 1]}{
				degdiffs.put(i, item); [i, item] };
		});

		"end of degrees to steps".postln;

		(scale.differentiate.reverse).do({ arg item, i;
			if(item==0){degdiffsrev.put(i, 1); [i, 1]}{
				degdiffsrev.put(i, item); [i, item] };
		});
		"end of degrees to steps rev".postln;

		scale.do({ arg item, i;  stepstoscaledeg.put(item, i); scaledeg.put(i, item); [i, item] });
		notediff = toAPitchCollection.tonic.keynum-aPC.keynum;


		//the note degree in the scale for the note to be transposed

		if(steps.isNegative){
			notedeg =(stepstoscaledeg[(notediff).abs%12]);
			notedeg.postln;

			//outnote is the number of steps from aPC that should be added to calc the transposed PC
			"calc descending steps".postln;

			(steps.abs).do({ arg item, i; outnote = (outnote)+ degdiffs[(notedeg-i)%degdiffs.size]; ["step index", notedeg-i %degdiffs.size, degdiffs[(notedeg-i)%degdiffs.size]] });
			outnote2 = PC2(aPC.keynum-outnote);


		}{

			notedeg =(stepstoscaledeg[(notediff).abs%12]);
			notedeg.postln;
			//outnote is the number of steps from aPC that should be added to calc the transposed PC
			"calc ascending steps".postln;
			steps.do({ arg item, i; outnote = (outnote)+ degdiffs[((notedeg+1)+i)%degdiffs.size]; ["step index", notedeg+i %degdiffs.size, degdiffs[(notedeg+i)%degdiffs.size]] });
			outnote2 = PC2(aPC.keynum+outnote);
			/*
			I need to get the keynum by adding the steps together..then find the corresponding scale degree and pitch name

			*/

		}




		^outnote2;

		/*
		[notenames[basenote], notenames[basenote1], toAPitchCollection.tonic.distanceFrom(aPC)[0], scale, (toAPitchCollection.tonic).distanceFrom(aPC)[0].mod, (toAPitchCollection.tonic.keynum-aPC.keynum).abs%12, scaledeg, stepstoscaledeg,( stepstoscaledeg[(toAPitchCollection.tonic.keynum-aPC.keynum).abs%12]), "notedeg", notedeg, notedeg +steps, scaledeg[notedeg +steps % scaledeg.size], "outnote", outnote

		];
		*/
		/*
		traverse the degree array by the number of steps from the o position.

		*/

	}


	/*

	distanceFrom2 gives the interval between two PCs. They are in absolute values and the intervals are expressed unpredictable

	for example:
	a major seventh may be expressed as a nil 1
	or a major third a diminished fourth...you will hve to check for this stuff later unless I keep hacking away


	Test:

	88.do{arg item, i;

	[PC2(64).distanceFrom2(PC2(95-70+i)),i].postln}

	PC2(64).distanceFrom2(PC2(76))
	build some harmonizers with these interval relationships

	ab eb bb f  c
	f b e a c
	e a  d  g  c
	d a e g c
	e a d g c

	things to do: build smarter harmonizers

	*/

	distanceFrom3 {arg aPC;
		var thisNote, thatNote, baseInterval, interval, direction, octaves, pf1, pf2;
		var halfsteps, idx, quality, out, addy=0, addy2=0,  addy3=0;
		thisNote = noteToScale[note];
		thatNote = noteToScale[aPC.note];
		(aPC.keynum > keynum).if({
			direction = \up;
			(thatNote < thisNote).if({
				thatNote = thatNote + 7;
			})
		}, {

			if((aPC.keynum == keynum)){direction = \unison;}{
				direction = \down;
				(thatNote > thisNote).if({
					thatNote = thatNote - 7;
				}
				)
			};
		});

		baseInterval = (thatNote - thisNote);
		halfsteps = (aPC.keynum - keynum).abs;
		interval = baseInterval.abs + 1;
		octaves = (halfsteps * 0.083333333333333).floor;
		[aPC.keynum, keynum, thisNote,thatNote,
			direction,
			"baseInterval", baseInterval, "halfsteps", halfsteps,"interval", interval, "octaves", octaves].postcs;
	}



	distanceFrom2 {arg aPC;
		var thisNote, thatNote, baseInterval, interval, direction, octaves, pf1, pf2;
		var halfsteps, idx, quality, out, addy=0, addy2=0,  addy3=0;
		thisNote = noteToScale[note];
		thatNote = noteToScale[aPC.note];
		(aPC.keynum > keynum).if({
			direction = \up;
			(thatNote < thisNote).if({
				thatNote = thatNote + 7;
			})
		}, {

			if((aPC.keynum == keynum)){direction = \unison;}{
				direction = \down;
				(thatNote > thisNote).if({
					thatNote = thatNote - 7;
				}
				)
			};
		});

		baseInterval = (thatNote - thisNote);
		halfsteps = (aPC.keynum - keynum).abs;
		interval = baseInterval.abs + 1;
		octaves = (halfsteps * 0.083333333333333).floor; //edit
		//	octaves = (freq.cpsoct.trunc - aPC.freq.cpsoct.trunc); //edit


		idx = (halfsteps % 12) - qualIdx[interval];
		quality = qualities[interval][idx];
		pf1 = freq.cpsoct.trunc;
		pf2 = aPC.freq.cpsoct.trunc;

		if( ((interval + (octaves * 7)) == 1) && ((aPC.keynum - keynum) ==0)){"truedog".postln;

			out = (freq.cpsoct.trunc - aPC.freq.cpsoct.trunc) ;

		}{"falsecat".postln;




			if(quality.asString=="perf"){

				if((aPC.keynum - keynum).asInteger.isNegative){

					if([12, 24, 36, 48, 60, 72].neg.includes((aPC.keynum - keynum).asInteger)){
						"true".postln;
						addy = 1;
						//addy = ( ([0,12, 24, 36, 48, 60, 72].indexOf((aPC.keynum - keynum).asInteger))).postln;

						/*
						addy = 1+([12, 24, 36, 48, 60, 72].indexOf(aPC.keynum - keynum));
						"addy".postln;

						addy.postln;
						*/
					}{};
					/*


					*/

				}{

					"perf".postln;
					(aPC.keynum - keynum).asInteger.postln;

					if([12, 24, 36, 48, 60, 72].includes((aPC.keynum - keynum).asInteger)){
						"true".postln;
						addy = 1;
						//addy = ( ([0,12, 24, 36, 48, 60, 72].indexOf((aPC.keynum - keynum).asInteger))).postln;

						/*
						addy = 1+([12, 24, 36, 48, 60, 72].indexOf(aPC.keynum - keynum));
						"addy".postln;

						addy.postln;
						*/
					}{};

				}{};




			}{};

			if((aPC.keynum - keynum).isNegative){
				if([1, 13, 25, 37, 49, 61, 73].neg.includes((aPC.keynum - keynum).asInteger)){
					"true".postln;
					quality = "minor";
					interval= interval+1;
				}{};
			}{};


		};


		if((quality.asString == "dim") && ((interval + (((octaves+addy) * 7)-addy2)%7) == 4 )){quality='major';
			interval=interval-1;
		}{};

		if((aPC.keynum - keynum).isPositive){


			if( ((aPC.keynum - keynum).asInteger % 12) == 11 ){quality='major';
				interval=interval+6;
			}{};


		}{};




		^[quality, interval + (((octaves+addy) * 7)-addy2), direction, octaves+addy-addy3, freq.cpsoct.trunc, aPC.freq.cpsoct.trunc, out, (aPC.keynum - keynum), (aPC.keynum - keynum)/12 ];
	}

	/*
	addy2 = 0;
	//addy2 = 3;

	/*

	if((aPC.keynum - keynum).isNegative){
	//if((halfsteps%12)==11){quality='major';}{};
	}{
	//if((halfsteps%12)==11){quality='major';
	//addy = 1;
	//addy2 = 1;
	//addy3 = 1;

	}{};
	};
	*/

	*/



	modalTranspose {arg steps = 0, fromAPitchCollection, toAPitchCollection;
		var degree = 0, pitchNames, idx = 0, test, size, notes, add, fromPC, toPC;
		var newNote, newPitch, newPC, newAcc, curAcc, degPStep, octAdd, scaleDist;
		var curNote, lastNote;
		octAdd = (steps / 7).floor;
		fromAPitchCollection = fromAPitchCollection ?? {PitchCollection.major(\c)};
		toAPitchCollection = toAPitchCollection ?? {fromAPitchCollection};
		fromPC = fromAPitchCollection.pitchCollection;
		toPC = toAPitchCollection.pitchCollection;
		size = fromPC.size;
		notes = fromPC.collect({arg me; me.note});
		test = false;
		while({
			(this.note == notes[degree]).if({
				test = true;
				add = ((this.keynum - fromPC[degree].keynum) % 12).asInteger;
				degPStep = degree + steps;
				newNote = notes[degPStep % 7];
				newPC = toPC[degPStep % 7];
				newAcc = sizeToAcc[(accToSize[newPC.acc] + add)];
				newPitch = (newNote ++ newAcc).asSymbol;
			}, {
				lastNote = noteToScale[notes[degree + steps % 7]];
				degree = degree + 1;
				curNote = noteToScale[notes[degree + steps % 7]];
				(curNote < lastNote).if	({octAdd = octAdd + 1});
			});
			(test == false and: {idx < size});
		});
		^this.class.new(newPitch, octave + octAdd); //
	}

	modalTranspose2 {arg steps = 0, fromAPitchCollection, toAPitchCollection;
		var degree = 0, pitchNames, idx = 0, test, size, notes, add, fromPC, toPC;
		var newNote, newPitch, newPC, newAcc, curAcc, degPStep, octAdd, scaleDist, finalnote, pcolldict=Dictionary.new;
		var curNote, lastNote;
		octAdd = (steps / 7).floor;

		"fromAPitchCollection".postln;

		fromAPitchCollection.postln;
		fromAPitchCollection = fromAPitchCollection ?? {PitchCollection.major(\c)};
		toAPitchCollection = toAPitchCollection ?? {fromAPitchCollection};
		fromPC = fromAPitchCollection.pitchCollection;
		"Hi".postln;
		toPC = toAPitchCollection.pitchCollection;
		size = fromPC.size;
		notes = fromPC.collect({arg me; me.note});
		test = false;

		toAPitchCollection.do({arg data;

			data.note.postln;
			data.pitch.postln;//
			pcolldict.add(data.note -> data.pitch);
		});
		pcolldict.postln;
		while({
			(this.note == notes[degree]).if({
				test = true;
				add = ((this.keynum - fromPC[degree].keynum) % 12).asInteger;
				degPStep = degree + steps;
				newNote = notes[degPStep % 7];
				"newn".postln;
				this.pitch.postln;
				newNote.postln;
				"finalnote".postln;
				pcolldict.at(newNote).postln;
				finalnote =
				newPC = toPC[degPStep % 7];
				newAcc = sizeToAcc[(accToSize[newPC.acc] + add)];
				"newacc".postln;
				newAcc.postln;
				newPitch = (newNote ++ newAcc).asSymbol;
			}, {
				lastNote = noteToScale[notes[degree + steps % 7]];
				degree = degree + 1;
				curNote = noteToScale[notes[degree + steps % 7]];
				(curNote < lastNote).if	({octAdd = octAdd + 1});
			});
			(test == false and: {idx < size});
		});
		"newp".postln;
		newPitch.postln;

		"octAdd".postln;
		octAdd.postln;
		"octave".postln;
		octave.postln;
		"pc".postln;
		this.keynum.postln;

		if(steps.isPositive){

			if(this.keynum<this.class.new(newPitch, octave + octAdd).keynum){"less than".postln;}{"greaterthan".postln;
				this.class.new(newPitch, octave + octAdd+1).pitch.postln;

				^this.class.new(newPitch, octave + octAdd+1);};

		}{

			if(this.keynum>this.class.new(newPitch, octave + octAdd).keynum){"less than".postln;}{"greaterthan".postln;
				this.class.new(newPitch, octave + octAdd).pitch.postln;

				^this.class.new(newPitch, octave + octAdd);};

		};
		this.class.new(newPitch, octave + octAdd).keynum.postln;
		this.class.new(newPitch, octave + octAdd).pitch.postln;
		"pcend".postln;
		^this.class.new(newPitch, octave + octAdd); //
	}





	modalTranspose3 {arg steps = 0, fromAPitchCollection, toAPitchCollection;
		var degree = 0, pitchNames, idx = 0, test, size, notes, add, fromPC, toPC;
		var newNote, newPitch, newPC, newAcc, curAcc, degPStep, octAdd, scaleDist, finalnote, pcolldict=Dictionary.new;
		var curNote, lastNote;
		octAdd = (steps / 7).floor;



		/*
		my guess is that

		*/
		"fromAPitchCollection".postln;



		fromAPitchCollection.postln;
		fromAPitchCollection = fromAPitchCollection ?? {PitchCollection.major(\c)};
		toAPitchCollection = toAPitchCollection ?? {fromAPitchCollection};
		fromPC = fromAPitchCollection.pitchCollection;
		"Hi".postln;
		toPC = toAPitchCollection.pitchCollection;
		size = fromPC.size;
		notes = fromPC.collect({arg me; me.note});
		test = false;


		while({
			(this.note == notes[degree]).if({
				test = true;
				add = ((this.keynum - fromPC[degree].keynum) % 12).asInteger;
				degPStep = degree + steps;
				newNote = notes[degPStep % 7];
				"newn".postln;
				this.pitch.postln;
				newNote.postln;
				"finalnote".postln;
				pcolldict.at(newNote).postln;
				finalnote =
				newPC = toPC[degPStep % 7];
				newAcc = sizeToAcc[(accToSize[newPC.acc] + add)];
				"newacc".postln;
				newAcc.postln;
				newPitch = (newNote ++ newAcc).asSymbol;
			}, {
				lastNote = noteToScale[notes[degree + steps % 7]];
				degree = degree + 1;
				curNote = noteToScale[notes[degree + steps % 7]];
				(curNote < lastNote).if	({octAdd = octAdd + 1});
			});
			(test == false and: {idx < size});
		});

		/*
		"newp".postln;
		newPitch.postln;
		*/
		"octAdd".postln;
		octAdd.postln;
		"octave".postln;
		this.octave.postln;

		"keynum".postln;

		this.keynum.postln;

		if(steps.isPositive){

			if(this.keynum<this.class.new(newPitch, octave + octAdd).keynum){"less than".postln;}{"greaterthan".postln;
				this.class.new(newPitch, octave + octAdd+1).pitch.postln;

				^this.class.new(newPitch, octave + octAdd);};

		}{

			if(this.keynum>this.class.new(newPitch, octave + octAdd).keynum){"less than".postln;}{"greaterthan".postln;
				this.class.new(newPitch, octave + octAdd).pitch.postln;

				^this.class.new(newPitch, octave + octAdd);};

		};
		this.class.new(newPitch, octave + octAdd).keynum.postln;
		this.class.new(newPitch, octave + octAdd).pitch.postln;
		"pcend".postln;
		^this.class.new(newPitch, octave + octAdd); //
	}



	// cn = c, cs = c-sharp, df = d-flat, dqf = d-quarter-flat, cqs = c-quarter-sharp
	// dtqf = d-three-quarter-flat, ctqs = c-three-quarter-sharp
	*initClass {
		noteToScale = IdentityDictionary[
			\c -> 0,
			\d -> 1,
			\e -> 2,
			\f -> 3,
			\g -> 4,
			\a -> 5,
			\b -> 6
		];
		scaleToNote = IdentityDictionary[
			0 -> \c,
			1 -> \d,
			2 -> \e,
			3 -> \f,
			4 -> \g,
			5 -> \a,
			6 -> \b
		];
		accToSize = IdentityDictionary[
			\ffff -> -4,
			\fff -> -3,
			\ff -> -2,
			//			\tqf -> -1.5,
			\f -> -1,
			//			\qf -> -0.5,
			\n -> 0,
			//			\qs -> 0.5,
			\s -> 1,
			//			\tqs -> 1.5,
			\ss -> 2,
			\sss -> 3,
			\ssss -> 4
		];
		sizeToAcc = IdentityDictionary[
			-4 -> \ffff,
			-3 -> \fff,
			-2 -> \ff,
			//			-1.5 -> \tqf,
			-1 -> \f,
			//			-0.5 -> \qf,
			0 -> \n,
			//			0.5 -> \qs,
			1 -> \s,
			//			1.5 -> \tqs,
			2 -> \ss,
			3 -> \sss,
			4 -> \ssss
		];
		notenames = IdentityDictionary[
			\bs -> 0,
			\cn -> 0,
			\dff -> 0,
			//			\cqs -> 0.5,
			//			\dtqf -> 0.5,
			\bss -> 1,
			\cs -> 1,
			\df -> 1,
			//			\ctqs -> 1.5,
			//			\dqf -> 1.5,
			\css -> 2,
			\dn -> 2,
			\eff -> 2,
			//			\dqs -> 2.5,
			//			\etqf -> 2.5,
			\ds -> 3,
			\ef -> 3,
			//			\dtqs -> 3.5,
			//			\eqf -> 3.5,
			\dss -> 4,
			\en -> 4,
			\ff -> 4,
			//			\eqs -> 4.5,
			//			\fqf -> 4.5,
			\es -> 5,
			\fn -> 5,
			\gff -> 5,
			//			\fqs -> 5.5,
			//			\gtqf -> 5.5,
			\fs -> 6,
			\gf -> 6,
			//			\ftqs -> 6.5,
			//			\gqf -> 6.5,
			\fss -> 7,
			\gn -> 7,
			\aff -> 7,
			//			\gqs -> 7.5,
			//			\atqf -> 7.5,
			\gs -> 8,
			\af -> 8,
			//			\gtqs -> 8.5,
			//			\aqf -> 8.5,
			\gss -> 9,
			\an -> 9,
			\bff -> 9,
			//			\aqs -> 9.5,
			//			\btqf -> 9.5,
			\as -> 10,
			\bf -> 10,
			//			\atqs -> 10.5,
			//			\bqf -> 10.5,
			\ass -> 11,
			\bn -> 11,
			\cf -> 11,
			//			\bqs -> 11.5,
			//			\cqf -> 11.5
			\rn -> -1
		];
		notenums = Dictionary[
			-1 -> \rn,
			0 -> \cn,
			//			0.5 -> [\c, \qs],
			1 -> \cs,
			//			1.5 -> [\c, \tqs],
			2 -> \dn,
			//			2.5 -> [\d, \qs],
			3 -> \ef,
			//			3.5 -> [\d, \tqs],
			4 -> \en,
			//			4.5 -> [\e, \qs],
			5 -> \fn,
			//			5.5 -> [\f, \qs],
			6 -> \fs,
			//			6.5 -> [\f, \tqs],
			7 -> \gn,
			//			7.5 -> [\g, \qs],
			8 -> \af,
			//			8.5 -> [\g, \tqs],
			9 -> \an,
			//			9.5 -> [\a, \qs],
			10 -> \bf,
			//			10.5 -> [\a, \tqs],
			11 -> \bn,
			//			11.5 -> [\b, \qs]
		];
		accToGuido = IdentityDictionary[
			\ffff -> "&&&&",
			\fff -> "&&&",
			\ff -> "&&",
			//			\tqf -> -1.5,
			\f -> "&",
			//			\qf -> -0.5,
			\n -> "",
			//			\qs -> 0.5,
			\s -> "#",
			//			\tqs -> 1.5,
			\ss -> "##",
			\sss -> "###",
			\ssss -> "####"
		];
		majScale = [0, 2, 4, 5, 7, 9, 11];
		qualities = IdentityDictionary[
			1 -> [\perf, \aug],
			2 -> [\dim, \minor, \major, \aug],
			3 -> [\dim, \minor, \major, \aug],
			4 -> [\dim, \perf, \aug],
			5 -> [\dim, \perf, \aug],
			6 -> [\dim, \minor, \major, \aug],
			7 -> [\dim, \minor, \major, \aug],
			8 -> [\dim, \perf, \aug]
		];

		qualIdx = IdentityDictionary[
			1 -> 0,
			2 -> 0,
			3 -> 2,
			4 -> 4,
			5 -> 6,
			6 -> 7,
			7 -> 9,
			8 -> 11,
		];
	}
}

PitchInterval2 {
	var <quality, <size, <mod;
	//quality is a symbol of \major, \minor, \perf, \dim or \aug
	// size is a
	*new {arg quality, size;
		var mod;
		// make sure size is an int, grab its mod
		mod = (size > 7).if({(size.round % 8) + 1}, {size});
		((mod == 1) || (mod == 4) || (mod == 5)).if({
			((quality ==  \perf) || (quality == \aug) || (quality == \dim)).if({
				^super.newCopyArgs(quality, size, mod)
			}, {
				"Unisons, fourths, fifths or octaves need to be \\perf or \\aug".warn;
				^nil;
			})
		}, {
			((mod == 2) || (mod == 3) || (mod == 6) || (mod == 7)).if({
				((quality == \major) || (quality == \minor) ||
					(quality == \dim) || (quality == \aug)).if({
					^super.newCopyArgs(quality, size, mod)
				}, {
					"Seconds, thirds, sixths or sevents need to be \\major, \\minor, \\dim or \\aug".warn;
					^nil;
				})
			})
		})
	}
}


/*
Progression {
	var <pitchCollectionArr, times;

	*new {arg pitchCollectionArr, times;
		pitchCollectionArr = pitchCollectionArr.asArray;
		^super.newCopyArgs(pitchCollectionArr, times);
	}
}
*/

PitchCollection2 {
	var <pitchCollection, <tonic, <octaveSize, <isScale, <pitchBase, <sortedBase;

	*new {arg pitchCollection, tonic, octaveSize = 12, isScale = false;
		pitchCollection = pitchCollection.asArray;
		tonic = tonic ? pitchCollection[0];
		^super.newCopyArgs(pitchCollection, tonic, octaveSize, isScale).init;
	}

	init {
		pitchBase = Array.fill(pitchCollection.size, {arg i; pitchCollection[i].pitchclass});
		sortedBase = pitchBase.copy.sort;
	}

	// will make scales and deal with note-names, transposition, string fomratting
	// random filtering

	*major {arg tonic;
		var steps, scale, start;
		start = tonic = tonic.notNil.if({
			tonic.isKindOf(PitchClass2).if({tonic}, {PitchClass2(tonic)})
		}, {
			PitchClass2.new(\c)
		});
		steps = [\major, \major, \minor, \major, \major, \major, \minor];
		steps.do{arg me, i;
			var note;
			note = start;
			start = start.transpose(PitchInterval2(me, 2), \up);
			steps[i] = note;
		};
		^this.new(steps, tonic, 12, true)
	}

	*minor {arg tonic;
		var steps, scale, start;
		start = tonic = tonic.notNil.if({
			tonic.isKindOf(PitchClass2).if({tonic}, {PitchClass2(tonic)})
		}, {
			PitchClass2.new(\c)
		});
		steps = [\major, \minor, \major, \major, \minor, \major, \major];
		steps.do{arg me, i;
			var note;
			note = start;
			start = start.transpose(PitchInterval2(me, 2), \up);
			steps[i] = note;
		};
		^this.new(steps, tonic, 12, true)
	}

	*natMinor {arg tonic;
		^this.minor(tonic);
	}

	*harmMinor {arg tonic;
		var steps, scale, start;
		start = tonic = tonic.notNil.if({
			tonic.isKindOf(PitchClass2).if({tonic}, {PitchClass2(tonic)})
		}, {
			PitchClass2.new(\c)
		});
		steps = [\major, \minor, \major, \major, \minor, \aug, \minor];
		steps.do{arg me, i;
			var note;
			note = start;
			start = start.transpose(PitchInterval2(me, 2), \up);
			steps[i] = note;
		};
		^this.new(steps, tonic, 12, true)
	}

	*ionian {arg tonic;
		^this.major(tonic);
	}

	*dorian {arg tonic;
		var steps, scale, start;
		start = tonic = tonic.notNil.if({
			tonic.isKindOf(PitchClass2).if({tonic}, {PitchClass2(tonic)})
		}, {
			PitchClass2.new(\c)
		});
		steps = [\major, \minor, \major, \major, \major, \minor, \major];
		steps.do{arg me, i;
			var note;
			note = start;
			start = start.transpose(PitchInterval2(me, 2), \up);
			steps[i] = note;
		};
		^this.new(steps, tonic, 12, true)
	}

	*phrygian {arg tonic;
		var steps, scale, start;
		start = tonic = tonic.notNil.if({
			tonic.isKindOf(PitchClass2).if({tonic}, {PitchClass2(tonic)})
		}, {
			PitchClass2.new(\c)
		});
		steps = [\minor, \major, \major, \major, \minor, \major, \major];
		steps.do{arg me, i;
			var note;
			note = start;
			start = start.transpose(PitchInterval2(me, 2), \up);
			steps[i] = note;
		};
		^this.new(steps, tonic, 12, true)
	}

	*lydian {arg tonic;
		var steps, scale, start;
		start = tonic = tonic.notNil.if({
			tonic.isKindOf(PitchClass2).if({tonic}, {PitchClass2(tonic)})
		}, {
			PitchClass2.new(\c)
		});
		steps = [\major, \major, \major, \minor, \major, \major, \minor];
		steps.do{arg me, i;
			var note;
			note = start;
			start = start.transpose(PitchInterval2(me, 2), \up);
			steps[i] = note;
		};
		^this.new(steps, tonic, 12, true)
	}

	*mixolydian {arg tonic;
		var steps, scale, start;
		start = tonic = tonic.notNil.if({
			tonic.isKindOf(PitchClass2).if({tonic}, {PitchClass2(tonic)})
		}, {
			PitchClass2.new(\c)
		});
		steps = [\major, \major, \minor, \major, \major, \minor, \major];
		steps.do{arg me, i;
			var note;
			note = start;
			start = start.transpose(PitchInterval2(me, 2), \up);
			steps[i] = note;
		};
		^this.new(steps, tonic, 12, true)
	}

	*aeolian {arg tonic;
		^this.minor(tonic);
	}

	*locrian {arg tonic;
		var steps, scale, start;
		start = tonic = tonic.notNil.if({
			tonic.isKindOf(PitchClass2).if({tonic}, {PitchClass2(tonic)})
		}, {
			PitchClass2.new(\c)
		});
		steps = [\minor, \major, \major, \minor, \major, \major, \major];
		steps.do{arg me, i;
			var note;
			note = start;
			start = start.transpose(PitchInterval2(me, 2), \up);
			steps[i] = note;
		};
		^this.new(steps, tonic, 12, true)
	}

	*chromatic {arg tonic;
		var start, steps, step, newoctave;
		tonic = tonic.notNil.if({
			tonic.isKindOf(PitchClass2).if({tonic}, {PitchClass2(tonic)})
		}, {
			PitchClass2.new(\c)
		});
		start = tonic.pitchclass;
		steps = Array.fill(12, {arg i;
			step = (i+start)%12;
			newoctave = tonic.octave + ((i+start) / 12).floor;
			PitchClass2.new(step, octave: newoctave);
		});
		^this.new(steps, tonic, 12, true);
	}

	// takes a keynum, returns the PitchClass closest to that keynum (under octave equivalence)
	filterKeynum {arg keynum;
		var baseKeynum, closestKey, member, thisSortedBase, thisIndex;
		// round out the octaves
		thisSortedBase = sortedBase ++ (sortedBase[0] + octaveSize);
		// get the mod for searching purposes
		baseKeynum = keynum % 12;
		// find the closes keynum's index in the sorted collection
		closestKey = (thisSortedBase).indexIn(baseKeynum);
		// find that closest keynums position in order of the collection
		thisIndex = pitchBase.indexOf(thisSortedBase[closestKey]%12);
		// figure out which note it is
		member = pitchCollection[thisIndex];
		// return a new PitchClass with the appropriate octave... check for nums close
		// to the next octave at C with the round
		^PitchClass2.new((member.note ++ member.acc), (keynum.round / 12).floor - 1);
	}

	distanceFromFilter {arg aPC1,aPC2, quantize=0;
		var out;
		if(quantize==0){
			^this.distantFrom4(this.filterKeynum(aPC1.keynum), this.filterKeynum(aPC2.keynum));
		}{
			^this.distantFrom4(aPC1, aPC2);
		};
	}

	distantFrom4 {arg aPC1,aPC2; //if not in key do something
		var pc_degrees = this.degrees, pc_degree, pc_degree2, diff = aPC2.keynum-aPC1.keynum, inc=0, step_inc=0, index=0,
		step_list=(pc_degrees++[12]).differentiate, direction;

		step_list.removeAt(0);
		pc_degree = aPC1.getScaleDegree(aPC1, this);
		pc_degree2 = aPC2.getScaleDegree(aPC2, this);

		index = pc_degree;
		"pc_degree".postcs;

		pc_degree.postcs;
		"index".postcs;
		index.postcs;
		pc_degrees.postcs;
		"index".postcs;

		if(aPC1.keynum==aPC2.keynum)
		{
			inc =0;
			direction = 'unison';
		}{
			if(aPC1.keynum < aPC2.keynum){
				//if the interval ascends

				direction = 'up';

				diff.postcs;

				while ( { step_inc < diff },
					{
						step_inc = step_inc + step_list[index % step_list.size];
						["index", index, "inc", inc, "halfsteps", pc_degrees[index % pc_degrees.size], "step_inc", step_inc].postln;

						index = index + 1;
						inc=inc+1;
					}
				);

			}{
				//if the interval descends
				direction = 'down';

				while ( { step_inc < diff.abs },
					{

						index = (index - 1).wrap(0,step_list.size-1);

						step_inc = step_inc + step_list[index];
						["index", index, "inc", inc, "halfsteps", step_inc, "step_inc", step_inc].postln;

						inc=inc+1;
					}
				);

			};
		};

		//^["pc_degree", pc_degree, "pc_degree2", pc_degree2, "pc_degrees", pc_degrees, "step_list",step_list, "steps", inc]
		^[inc, direction]
	}


	at {arg idx;
		^pitchCollection[idx]
	}

	copySeries {arg first, second, last;
		^this.class.new(pitchCollection.copySeries(first, second, last), tonic, octaveSize)
	}

	do {arg func;
		pitchCollection.do({arg me, i;
			func.value(me, i);
		})
	}
	choose {
		^pitchCollection.choose;
	}

	wchoose {arg weights;
		^pitchCollection.wchoose(weights);
	}

	chunk {arg start = 0, end;
		end = end ?? pitchCollection.size - 1
		^this.class.new(pitchCollection[start..end], tonic, 12);
	}

	add {arg aPitchClass;
		^this.class.new(pitchCollection ++ aPitchClass, tonic, 12)
	}

	invert {arg aPitchClass;
		^this.class.new(pitchCollection.collect({arg me; me.invert(aPitchClass)}));
	}

	insert {arg position = 0, aPitchClass;
		^this.class.new(pitchCollection.insert(position, aPitchClass).flat, tonic, 12)
	}

	transpose {arg aPitchInterval, direction = \up;
		^this.class.new(Array.fill(pitchCollection.size, {arg i;
			pitchCollection[i].transpose(aPitchInterval, direction)}),
		tonic.transpose(aPitchInterval, direction), octaveSize)
	}

	degrees {arg idx;
		var a = LinkedList.new;
		pitchCollection.do{arg item, i;
			a.add(item.keynum.asInteger);
		};
		a = a.differentiate.asArray;
		a.removeAt(0);
		a=a.integrate;
		^[0]++a
	}

	/*modalTranspose {arg steps, fromAPitchCollection, toAPitchCollection;
	^this.class.new(Array.fill(pitchCollection.size, {arg i;
	pitchCollection[i].modalTranspose(steps, fromAPitchCollection, toAPitchCollection)}),
	tonic.modalTranspose(steps, fromAPitchCollection, toAPitchCollection))
	}
	*/

	//	modalTranspose {arg steps = 0, aPitchCollection, direction = \up;
	//		var newCollection, newTonic, baseInterval;
	//		newCollection = Array.fill(pitchCollection.size,
	//				{arg i; pitchCollection[i].transpose(aPitchInterval, direction)});
	//		newTonic = tonic.transpose(aPitchInterval, direction);
	//		^this.class.new(newCollection, newTonic, octaveSize)
	//		}


	/*
	*quartertone {arg tonic;
	var start, steps, step, newoctave;
	start = tonic.pitchclass;
	steps = Array.fill(24, {arg i;
	step = ((i * 0.5)+start)%12;
	newoctave = tonic.octave + (((i*0.5)+start) / 12).floor;
	PitchClass.new(step, octave: newoctave);
	});
	^this.new(steps, tonic, 12);
	}
	*/

	/*
	*octatonic {arg tonic = 0; ^this.new([0, 2, 3, 5, 6, 8, 9, 11], tonic)}
	*octatonic013 {arg tonic = 0; ^this.new([0, 1, 3, 4, 6, 7, 9, 10], tonic)}
	*octatonic023 {arg tonic = 0; ^this.octatonic(tonic)}

	*/

}

PC2 : PitchClass2 { }
PI2 : PitchInterval2 { }
PColl2 : PitchCollection2 { }