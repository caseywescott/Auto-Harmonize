

VoicingTable {

	var
	<>table,
	<>database;

	// constructor method
	*new {

		^super.new.init()
	}

	init {
		this.loadTables;
	}

	loadTables{
		table = [
			[
				"two part 1st species2",
				[
					[0,4,7],
					[2,4,5,7,9],
					[0,7]
				]
			],
			[
				"two part 1st species",
				[
					[0,4,7],
					[2,4,5,7,9],
					[0,7]
				]
			],
			[
				"two part 1st species no 10th",
				[
					[0,4,7],
					[2,4,5,7,9],
					[0,7]
				]
			],
			[
				"two part 2nd species no 10th",
				[
					[0,4,7], //beginning
					[2,4,5,7], //strong beat
					[2,3,4,5,7],//weak beat
					[0,7] //end
				]
			],
			[
				"three part 1st species",
				[
					[
						[0,7],[3,4], [5,2]
					],
					[
						[2,2],[2,5], [2,7], [2,9],
						[3, 4],
						[4, 7],
						[5,2], [5, 4], [5,9],
						[7, 2], [7, 4], [7, 9]
					],
					[
						[0,7],
						[2,7],
						[3,11],[3,4],[3,11],
						[4,0],[4,7],
						[5,9],[5,2],
						[7,7]
					]
				],
				[
					[3,2],[3,9],
					[4,5],
					[5,2],[5,12]
				]
			],
			[
				"four part 1st species",
				[
					[
						[2,2,7],[2,3,4], [2,5,2], [2,5,9], [2,7,2], [2,7,9],
						[3,2,2],[3,2,9],
						[4,5,2],[4,5,9],
						[5,2,2],[5,2,9],[5,4,2],[5,4,7],[5,5,4],
						[7,2,7],[7,5,4]
					],
					[
						[2,2,5],[2,3,9], [2,7,5],
						[3,2,7],[3,4,5],
						[4,5,7],[4,7,5],
						[5,2,5],[5,4,5],[5,5,2],[5,5,9],
						[7,3,2],[7,3,9], [7,5,7]
					]
				]
			],
			[
				"five part 1st species",
				[
					[
						[2,2,2,2], [2,2,2,7], [2,3,3,3], [2,3,4,2], [2,5,3,6], [2,5,5,6], [2,7,3,6],
						[3,2,2,2],[3,2,4,2], [3,2,2,9],[3,3,3,2],[3,3,3,3],[3,4,5,4],
						[4,2,3,4],[4,3,2,7],[4,5,3,6],[4,5,4,7],
						[5,2,2,7],[5,4,2,4],[5,4,2,7],[5,4,7,2],[5,6,5,2],
						[7,2,2,2],[7,3,2,4]
					],
					[
						[2,2,5,3],[2,3,4,9], [2,7,2,7],
						[3,2,7,2],[3,4,5,7],
						[4,5,3,4],[4,5,3,6],[4,7,3,2],[4,7,5,5],
						[5,2,2,2],[5,2,2,7],[5,4,5,4],[5,5,2,2],[5,5,2,5],
						[7,2,2,9],[7,2,4,5],[7,3,2,7],[7,4,5,4]
					]
				]
			],
			[
				"five part 1st species2",
				[
					[
						[2,2,2,2],  [2,3,3,3], [2,3,4,2],
						[3,2,2,2],[3,2,4,2], [3,2,2,9],[3,3,3,2],[3,3,3,3],[3,4,5,4],
						[4,2,3,4]
					],
					[
						[2,2,5,3],
						[3,2,7,2],
						[4,5,3,4],[4,5,3,6]
					]
				]
			],
			[
				"five part 1st species3",
				[
					[
						[2,2,2,2],  [2,3,3,3], [3,3,3,3]
					],
					[
						[2,2,5,3]

					]
				]
			],
			[
				"Ravel StrQt 4",
				[
					[
						[ 2, 2, 5 ], [ 2, 3, 6 ], [ 2, 6, 3 ],
						[ 3, 2, 3 ], [ 3, 2, 4 ], [ 3, 3, 6 ], [ 3, 5, 4 ], [ 3, 6, 3 ], [ 3, 6, 4 ], [ 3, 6, 5 ],
						[ 4, 2, 3 ], [ 4, 4, 5 ], [ 4, 5, 4 ],
						[ 5, 4, 4 ], [ 5, 6, 3 ],
						[ 6, 2, 3 ],[ 6, 2, 6 ], [ 6, 4, 5 ], [ 6, 5, 4 ], [ 6, 6, 3 ],
						[ 7, 2, 3 ], [ 7, 5, 4 ]
					],
					[
						[ 2, 2, 5 ], [ 2, 6, 3 ],
						[ 3, 2, 3 ], [ 3, 2, 4 ], [ 3, 5, 4 ], [ 3, 6, 3 ], [ 3, 6, 4 ], [ 3, 6, 5 ],
						[ 4, 2, 3 ], [ 4, 4, 5 ], [ 4, 5, 4 ],
						[ 5, 4, 4 ],

						[ 7, 2, 3 ], [ 7, 5, 4 ]
					]
				]
			]




		];

		//convert table to dictionary to access by voicing table name
		database = Dictionary.new;
		table.do({arg item, i;
			database.put(item[0], item[1]);
		});
	}


	get_n_part_voicingsPC_and_Keynum{arg cantus = PC(69), voice = 0, firstlast_or_middle = 0, pc = PColl.lydian(\a), new_voice_table_set = [[0,4,7],[2,4,5,7,9],[0,7]];
		var index, temptable;
		temptable = this.get_n_part_voicingsPC_and_KeynumTable(cantus, voice, firstlast_or_middle, pc, new_voice_table_set);
		index = Array.fill( temptable[0].size, {arg i; i;}).choose;
		^[temptable[0][index],temptable[1][index]];
	}


	get_n_part_voicingsPC_and_KeynumTable{arg cantus = PC(69), voice = 0, firstlast_or_middle = 0, pc = PColl.lydian(\a), new_voice_table_set = [[0,4,7],[2,4,5,7,9],[0,7]];
		var temp_val, outlist4 = LinkedList.new, outlist3 = LinkedList.new, outlist2, outlist = LinkedList.new, pcoutlist, keynumlist = LinkedList.new, voicing_table_first = [0,4,7], tkeynumlist = LinkedList.new, t1,t2,t3,t4,t5,t6,
		voicing_table_first_last = [0,4,7],voicing_table_last = [0,7], maxtablesize, maxtableindex;

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

		//	"maxtableindex".postcs;
		//	maxtableindex.postcs;

		//get the table to iterate
		if(firstlast_or_middle >= new_voice_table_set.size){

			temp_val = new_voice_table_set[maxtableindex];
		}{
			temp_val = new_voice_table_set[firstlast_or_middle];
		};

		//	"temp_val".postcs;
		//cantus.keynum.postcs;
		//temp_val.postcs;



		temp_val.do({arg item, i;

			outlist2 = LinkedList.new;
			pcoutlist = LinkedList.new;

			if(item.isArray == false){
				"process separately".postcs;
				if(voice == 0){
					pcoutlist.add(cantus);
					outlist2.add(cantus.keynum);
					pcoutlist.add(PC(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item.neg)[4].keynum));
					outlist2.add(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item.neg)[4].keynum);
				}{
					pcoutlist.add(PC(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item)[4].keynum));
					outlist2.add(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item)[4].keynum);
					pcoutlist.add(cantus);
					outlist2.add(cantus.keynum);
				};
			}{

				if(voice == 0){
					pcoutlist.add(cantus);
					outlist2.add(cantus.keynum);

					"heree 1".postcs;
					t1 = (Array.fill(item.size, { arg i;
						(item.copyRange(0,i).sum);
					}));

					"t1".postcs;
					t1.postcs;

					t1.do({arg item2, i2;
						(cantus.keynum + item2);

						pcoutlist.add(cantus);
						outlist2.add(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item2.neg)[4].keynum);
					});

					"heree 2".postcs;
				}{

					"item.size".postcs;
					item.size.postcs;

					if(voice == (item.size )){

						"heree 1".postcs;
						t1 = (Array.fill(item.size, { arg i;
							(item.reverse.copyRange(0,i).sum);
						}));

						"t1".postcs;
						t1.reverse.postcs;

						t1.reverse.do({arg item2, i2;
							(cantus.keynum + item2);

							pcoutlist.add(PC(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item2)[4].keynum));
							outlist2.add(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item2)[4].keynum);
						});

						pcoutlist.add(cantus.keynum);
						outlist2.add(cantus.keynum);

						"heree 2".postcs;

					}{

						"the middle voicings".postcs;

						"the item values above the voicing".postcs;

						//t1 = (Array.fill(item.size, { arg i;
						//	[item, item.copyRange(0,voice-1), (item.copyRange(0,voice-1).sum)].postcs;
						//}));
						t1 = item.copyRange(0,voice-1);


						"t1".postcs;
						t1.postcs;

						t3 = (Array.fill(t1.size, { arg i;
							//	[item, item.copyRange(0,voice-1), (item.copyRange(0,voice-1).sum)].postcs;
							t1.reverse.copyRange(0,i).sum;
						}));

						"t3".postcs;
						t3.reverse.postcs;

						t3.reverse.do({arg item12, i12;

							pcoutlist.add(PC(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item12)[4].keynum));
							outlist2.add(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item12)[4].keynum);
						});

						pcoutlist.add(cantus);
						outlist2.add(cantus.keynum);

						t4 = item.copyRange(voice, item.size-1);
						"t4".postcs;
						t4.postcs;

						t2 = (Array.fill(t4.size, { arg i;
							//[item, item.copyRange(voice, item.size-1), item.copyRange(voice, item.size-1).sum].postcs;
							//[item, item.copyRange(voice, item.size-1), item.copyRange(voice, item.size-1).sum].postcs;
							t4.copyRange(0,i).sum;
						}));

						"t2".postcs;
						t2.postcs;
						"item".postcs;
						item.postcs;

						"the item values above the voicing2".postcs;

						t2.do({arg item2, i2;

							pcoutlist.add(PC(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item2.neg)[4].keynum));
							outlist2.add(cantus.finalModalTranspose(pc.filterKeynum(cantus.keynum), pc, item2.neg)[4].keynum);
						});
						/*

						"steps from cantus note to lowest note".postcs;
						(Array.fill(item.size, { arg i;
						[item.copyRange(i,item.size-1), (item.copyRange(i,item.size-1).sum)].postcs;
						}));
						"steps from cantus note to highet note".postcs;
						*/
					};
				};
			};
			outlist4.add(pcoutlist.asArray);

			outlist3.add(outlist2.asArray);
			"outlist3".postcs;
			outlist3.postcs;
			"outlist3 end".postcs;

		});



		/*
		Array.fill(temp_val.size, { arg i;
		temp_val.copyRange(0,i).sum.postcs;
		});
		*/
		if(voice = 0){

			//	[2,5] -- [2,7] [2,5,2] --- [2,7,9] Array


		}{
			//Array.fill(, { arg i; i * 2 });
		};


		"tkeynumlist".postcs;

		tkeynumlist.postcs;
		"outlist3".postcs;
		keynumlist = outlist3.postcs;
		^[outlist4.asArray, keynumlist.asArray].postcs;
	}
}


