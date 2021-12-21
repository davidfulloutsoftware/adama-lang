package org.adamalang;

/** centralized listing of all error codes */
public class ErrorCodes {
    public final static int DURABLE_LIVING_DOCUMENT_STAGE_FRESH_DRIVE = 123392;
    public final static int DURABLE_LIVING_DOCUMENT_STAGE_FRESH_PERSIST = 198657;
    public final static int DURABLE_LIVING_DOCUMENT_STAGE_CONSTRUCT_DRIVE = 130568;
    public final static int DURABLE_LIVING_DOCUMENT_STAGE_CONSTRUCT_PERSIST = 134152;
    public final static int DURABLE_LIVING_DOCUMENT_STAGE_LOAD_DRIVE = 143880;
    public final static int DURABLE_LIVING_DOCUMENT_STAGE_LOAD_READ = 101386;
    public final static int DURABLE_LIVING_DOCUMENT_STAGE_ATTACH_PRIVATE_VIEW = 138255;
    public static final int LIVING_DOCUMENT_TRANSACTION_NO_COMMAND_FOUND = 194575;
    public static final int LIVING_DOCUMENT_TRANSACTION_UNRECOGNIZED_FIELD_PRESENT = 184335;
    public static final int LIVING_DOCUMENT_TRANSACTION_ALREADY_CONNECTED = 115724;
    public static final int LIVING_DOCUMENT_TRANSACTION_ALREADY_CONSTRUCTED = 132111;
    public static final int LIVING_DOCUMENT_TRANSACTION_CANT_DISCONNECT_DUE_TO_NOT_CONNECTED = 145423;
    public static final int LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_CHANNEL = 160268;
    public static final int LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_MESSAGE = 184332;
    public static final int LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NOT_CONNECTED = 143373;
    public static final int LIVING_DOCUMENT_TRANSACTION_CANT_ATTACH_NOT_CONNECTED = 125966;
    public static final int LIVING_DOCUMENT_TRANSACTION_CLIENT_REJECTED = 184333;
    public static final int LIVING_DOCUMENT_TRANSACTION_NO_CLIENT_AS_WHO = 122896;
    public static final int LIVING_DOCUMENT_TRANSACTION_NO_LIMIT = 146448;
    public static final int LIVING_DOCUMENT_TRANSACTION_NO_CONSTRUCTOR_ARG = 196624;
    public static final int LIVING_DOCUMENT_TRANSACTION_NO_TIMESTAMP = 143889;
    public static final int LIVING_DOCUMENT_TRANSACTION_NO_VALID_COMMAND_FOUND = 132116;
    public static final int LIVING_DOCUMENT_TRANSACTION_NO_ASSET = 143380;
    public static final int LIVING_DOCUMENT_TRANSACTION_NO_PATCH = 193055;
    public static final int LIVING_DOCUMENT_TRANSACTION_MESSAGE_ALREADY_SENT = 143407;
    public static final int LIVING_DOCUMENT_TRANSACTION_EXPIRE_LIMIT_MUST_BE_POSITIVE = 122412;
    public static final int FACTORY_CANT_BIND_JAVA_CODE = 198174;
    public static final int FACTORY_CANT_COMPILE_JAVA_CODE = 180258;
    public static final int FACTORY_CANT_CREATE_OBJECT_DUE_TO_CATASTROPHE = 115747;
    public static final int CATASTROPHIC_DOCUMENT_FAILURE_EXCEPTION = 144416;
    public static final int SERVICE_DOCUMENT_ALREADY_CREATED = 130092;
    public static final int USERLAND_CANT_COMPILE_ADAMA_SCRIPT = 144417;
    public static final int DOCUMENT_SELF_DESTRUCT_SUCCESSFUL = 134195;
    public static final int INMEMORY_DATA_GET_CANT_FIND_DOCUMENT = 198705;
    public static final int INMEMORY_DATA_INITIALIZED_UNABLE_ALREADY_EXISTS = 116787;
    public static final int INMEMORY_DATA_PATCH_CANT_FIND_DOCUMENT = 144944;
    public static final int INMEMORY_DATA_COMPUTE_CANT_FIND_DOCUMENT = 106546;
    public static final int INMEMORY_DATA_COMPUTE_REWIND_NOTHING_TODO = 128052;
    public static final int INMEMORY_DATA_COMPUTE_UNSEND_FAILED_TO_FIND = 194612;
    public static final int INMEMORY_DATA_DELETE_CANT_FIND_DOCUMENT = 117816;
    public static final int INMEMORY_DATA_COMPUTE_INVALID_METHOD = 127034;

    public static final int UNCAUGHT_EXCEPTION_WEB_SOCKET = 295116;
    public static final int ONLY_ACCEPTS_TEXT_FRAMES = 213711;
    public static final int USERLAND_REQUEST_NO_METHOD_PROPERTY = 213708;
    public static final int USERLAND_REQUEST_NO_ID_PROPERTY = 233120;

    public static final int INITIALIZE_FAILURE = 667658;
    public static final int PATCH_FAILURE = 640009;
    public static final int COMPUTE_FAILURE = 605195;
    public static final int GET_FAILURE = 669710;
    public static final int DELETE_FAILURE = 641036;
    public static final int PATCH_LATE_SEQ_NOT_INC = 621580;
    public static final int SCAN_FAILURE = 643084;
    public static final int COMPUTE_EMPTY_REWIND = 694287;
    public static final int COMPUTE_EMPTY_UNSEND = 650252;
    public static final int COMPUTE_UNKNOWN_METHOD = 656396;
    public static final int LOOKUP_FAILED = 625676;

    /**
     *      * 688141
     *      * 601088
     *      * 634880
     *      * 643072
     *      * 654339
     *      * 662528
     */

    public static final int FRONTEND_SPACE_ALREADY_EXISTS = 679948;
    public static final int FRONTEND_SPACE_DOESNT_EXIST = 625678;
    public static final int FRONTEND_PLAN_DOESNT_EXIST = 609294;

    /**
     *
     * 117818
     * 155711
     * 126012
     * 176703
     * 151615
     * 120895
     * 145980
     * 117823
     * 132157
     * 134214
     * 143430
     * 116812
     * 115788
     * 143948
     * 199768
     * 120944
     * 127088
     * 134259
     * 180858
     * 146558
     * 148095
     * 123004
     * 192639
     * 159869
     * 198786
     * 131203
     * 146561
     * 127106
     * 146569
     * 146575
     * 103060
     * 117923
     * 127152
     * 127155
     * 133308
     * 129727
     * 114881
     * 187586
     * 127169
     * 197827
     * 168131
     * 161987
     * 146115
     * 194752
     * 121027
     * 130242
     * 144583
     * 146631
     * 183498
     * 199883
     * 116936
     * 115917
     * 127692
     * 199886
     * 109775
     * 139469
     * 128208
     * 114384
     * 145627
     * 113884
     * 197852
     * 199907
     * 191713
     * 180978
     * 147186
     * 120048
     * 177395
     * 110832
     * 193267
     * 193264
     * 111347
     * 197872
     * 193265
     * 131825
     * 127732
     * 162036
     * 127736
     * 133371
     * 145659
     * 134399
     * 120060
     * 184063
     * 125180
     * 123644
     * 130303
     * 136444
     * 165117
     * 127745
     * 182531
     * 149251
     * 148736
     * 130819
     * 100100
     * 118020
     * 135940
     * 131845
     * 195851
     * 131855
     * 134927
     * 122124
     * 101135
     * 135948
     * 196876
     * 130833
     * 197905
     * 195871
     * 131356
     * 198434
     * 144161
     * 127780
     * 131879
     * 180004
     * 134954
     * 118573
     * 134958
     * 182578
     * 101680
     * 195891
     * 103219
     * 199472
     * 127794
     * 148279
     * 114484
     * 199995
     * 140088
     * 143166
     * 180031
     * 142143
     * 171839
     * 115518
     * 107328
     * 131904
     * 197447
     * 196939
     * 196936
     * 131919
     * 183116
     * 196448
     * 110953
     * 195951
     * 123760
     * 160115
     * 145267
     * 138096
     * 131964
     * 111487
     * 199043
     * 115584
     * 134016
     * 115592
     * 118669
     * 116623
     * 196492
     * 180109
     * 114582
     * 135070
     * 147363
     * 144288
     * 197548
     * 146354
     * 163763
     * 149427
     * 114608
     * 118707
     * 147376
     * 184258
     * 163778
     * 138179
     * 142787
     * 144320
     * 180160
     * 163776
     * 133056
     * 127938
     * 146881
     * 177095
     * 146887
     * 143818
     * 196555
     * 193481
     * 117709
     * 151503
     * 111564
     * 118735
     * 134099
     * 150483
     * 151504
     * 184280
     * 188383
     * 165852
     * 118752
     * 151011
     * 123875
     * 139745
     * 183781
     * 198639
     * 189423
     * 129007
     * 194547
     * 193011
     * 189424
     * 133105
     * 130040
     * 193534
     * 118781
     * 142847
     * 119804
     * 167423
     * 133119
     * 131068
     * 184319
     * 130559
     * 148477
     */

    /**

     * 602115
     * 626691
     * 654341
     * 684039
     * 605208
     * 662552
     * 643100
     * 695327
     * 647199
     * 642079
     * 604191
     * 656403
     * 634899
     * 674832
     * 634900
     * 643095
     * 605227
     * 639018
     * 668719
     * 684079
     * 602158
     * 642094
     * 622624
     * 605216
     * 618532
     * 605223
     * 687163
     * 640056
     * 654392
     * 656443
     * 639034
     * 654394
     * 684089
     * 662591
     * 603196
     * 650300
     * 666687
     * 629822
     * 691261
     * 652350
     * 684082
     * 620592
     * 601136
     * 658483
     * 605235
     * 678960
     * 688176
     * 635955
     * 605234
     * 651316
     * 625716
     * 688180
     * 647244
     * 649292
     * 606284
     * 638028
     * 691279
     * 639052
     * 655436
     * 630851
     * 605251
     * 658500
     * 639059
     * 629868
     * 639072
     * 654459
     * 640124
     * 653436
     * 605311
     * 656499
     * 659568
     * 622707
     * 629900
     * 671887
     * 620687
     * 665740
     * 605327
     * 602255
     * 667789
     * 639105
     * 670852
     * 651416
     * 655504
     * 642235
     * 654523
     * 651453
     * 622768
     * 659632
     * 653490
     * 640183
     * 646344
     * 606411
     * 646347
     * 654539
     * 602317
     * 641229
     * 654540
     * 655567
     * 671951
     * 601292
     * 697548
     * 681164
     * 606401
     * 603329
     * 622787
     * 626883
     * 602307
     * 608450
     * 642245
     * 602308
     * 688327
     * 639175
     * 651463
     * 651462
     * 662725
     * 687323
     * 652507
     * 613596
     * 667859
     * 645328
     * 670928
     * 688336
     * 691408
     * 658647
     * 691412
     * 687338
     * 603368
     * 688363
     * 651499
     * 687342
     * 649452
     * 622828
     * 667884
     * 605422
     * 651489
     * 653536
     * 657635
     * 691424
     * 659680
     * 602339
     * 604386
     * 684263
     * 655610
     * 658682
     * 657658
     * 688376
     * 618749
     * 625917
     * 667903
     * 651516
     * 688383
     * 654590
     * 652542
     * 616689
     * 689394
     * 644337
     * 602353
     * 685299
     * 669939
     * 668915
     * 680179
     * 645360
     * 670960
     * 662768
     * 675056
     * 646386
     * 696561
     * 653554
     * 670966
     * 642292
     * 618743
     * 652535
     * 635126
     * 642313
     * 654600
     * 675083
     * 609548
     * 670991
     * 667916
     * 691468
     * 602382
     * 657677
     * 642311
     * 641311
     * 675091
     * 605484
     * 625964
     * 606511
     * 675123
     * 657712
     * 639282
     * 653620
     * 651592
     * 639311
     * 615744
     * 605506
     * 671091
     * 666992
     * 688496
     * 668044
     * 655746
     * 675203
     * 642439
     * 639391
     * 630174
     * 627104
     * 604576
     * 643519
     * 652735
     * 606640
     * 672176
     * 655792
     * 658890
     * 602568
     * 640461
     * 650701
     * 656847
     * 667072
     * 640451
     * 602562
     * 655839
     * 638428
     * 658900
     * 658927
     * 654831
     * 695779
     * 668154
     * 647676
     * 699888
     * 651763
     * 651762
     * 642548
     * 654861
     * 641549
     * 667151
     * 606735
     * 647695
     * 642575
     * 658957
     * 688642
     * 635392
     * 676355
     * 646659
     * 639491
     * 623107
     * 669185
     * 671238
     * 642576
     * 655912
     * 604716
     * 640559
     * 658983
     * 695844
     * 639544
     * 602683
     * 655935
     * 667199
     * 606780
     * 687676
     * 684604
     * 650814
     * 643633
     * 691762
     * 625200
     * 672307
     * 638515
     * 685616
     * 655927
     * 654903
     * 699993
     * 606844
     * 659087
     * 669324
     * 638592
     * 651907
     * 602800
     * 639666
     * 671438
     * 624332
     * 641743
     * 672460
     * 656064
     * 621251
     * 668352
     * 656065
     * 672455
     * 639696
     * 635628
     * 656108
     * 640743
     * 611065
     * 610040
     * 656124
     * 668413
     * 667379
     * 606963
     * 688880
     * 652018
     * 672500
     * 602889
     * 671498
     * 639752
     * 644872
     * 684808
     * 603915
     * 668424
     * 642826
     * 684814
     * 654093
     * 664335
     * 687887
     * 660239
     * 659212
     * 604943
     * 654095
     * 622351
     * 639745
     * 638720
     * 673539
     * 605952
     * 626435
     * 616194
     * 668417
     * 667396
     * 646919
     * 623384
     * 652059
     * 671519
     * 643868
     * 652049
     * 659218
     * 663315
     * 646931
     * 666384
     * 684816
     * 652051
     * 658193
     * 642861
     * 638765
     * 662319
     * 641836
     * 646956
     * 688940
     * 605999
     * 652078
     * 671522
     * 655136
     * 641824
     * 660256
     * 622371
     * 639783
     * 655161
     * 675640
     * 647997
     * 618300
     * 660287
     * 610111
     * 607039
     * 603953
     * 623409
     * 664370
     * 626480
     * 670515
     * 694067
     * 608048
     * 663344
     * 668464
     * 610099
     * 658224
     * 649011
     * 684854
     * 680759
     * 603956
     * 639799
     * 602935
     * 606006
     * 675637
     * 655176
     * 671567
     * 602956
     * 680783
     * 646976
     * 606016
     * 651075
     * 659283
     * 655213
     * 668526
     * 601967
     * 644988
     * 646012
     * 639871
     * 626559
     * 652145
     * 623472
     * 648051
     * 643954
     * 655240
     * 642955
     * 647055
     * 641934
     * 654208
     * 651136
     * 623491
     * 662400
     * 606083
     * 656260
     * 642968
     * 675744
     * 603064
     * 689083
     * 697279
     * 675775
     * 641983
     * 691123
     * 689075
     * 663475
     * 642996
     * 664503
     * 622537
     * 671691
     * 601035
     * 660430
     * 638925
     * 695246
     * 628684
     * 656332
     * 623567
     * 652239
     * 653263
     * 651215
     * 670668
     * 608207
     * 667597
     * 640974
     * 639937
     * 619456
     * 690115
     * 669635
     * 662467
     * 624579
     * 623555
     * 692160
     * 648130
     * 630722
     * 652231
     * 673732
     * 693188
     * 603096
     * 654300
     * 648159
     * 625631
     * 638942
     * 655314
     * 689108
     * 651240
     * 672751
     * 663532
     * 638959
     * 626670
     * 659427
     * 603107
     * 635879
     * 619512
     * 668665
     * 643066
     * 623613
     * 605180
     * 669695
     * 696316
     * 656380
     * 635903
     * 625663
     * 684029
     * 653310
     * 652286
     * 605182
     * 692221
     * 654321
     * 641008
     * 685043
     * 606195
     * 626675
     * 698352
     * 641010
     * 635893
     * 605172
     * 691188
     */
}
