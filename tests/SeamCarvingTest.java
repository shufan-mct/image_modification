import org.junit.Before;
import org.junit.Test;

import javax.imageio.ImageIO;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SeamCarvingTest {
    private SeamCarving sc;
    private int height, width;

    @Before
    public void setUp () throws IOException {
        sc = new SeamCarving();
        sc.readImage("balloon-sky.jpg");
        height = sc.getHeight();
        width = sc.getWidth();
    }

    @Test
    public void getHVneighbors () {
        ArrayList<Position> ns;

        ns = sc.getHVneighbors(0,0);
        assertTrue(ns.contains(new Position(1,0)));
        assertTrue(ns.contains(new Position(0,1)));
        assertEquals(2, ns.size());

        ns = sc.getHVneighbors(0,width/2);
        assertTrue(ns.contains(new Position(0,width/2-1)));
        assertTrue(ns.contains(new Position(0,width/2+1)));
        assertTrue(ns.contains(new Position(1,width/2)));
        assertEquals(3, ns.size());

        ns = sc.getHVneighbors(0,width-1);
        assertTrue(ns.contains(new Position(0,width-2)));
        assertTrue(ns.contains(new Position(1,width-1)));
        assertEquals(2, ns.size());

        ns = sc.getHVneighbors(height/2,0);
        assertTrue(ns.contains(new Position(height/2-1,0)));
        assertTrue(ns.contains(new Position(height/2+1,0)));
        assertTrue(ns.contains(new Position(height/2, 1)));
        assertEquals(3, ns.size());

        ns = sc.getHVneighbors(height/2,width/2);
        assertTrue(ns.contains(new Position(height/2-1,width/2)));
        assertTrue(ns.contains(new Position(height/2+1,width/2)));
        assertTrue(ns.contains(new Position(height/2, width/2-1)));
        assertTrue(ns.contains(new Position(height/2, width/2+1)));
        assertEquals(4, ns.size());

        ns = sc.getHVneighbors(height/2,width-1);
        assertTrue(ns.contains(new Position(height/2,width-2)));
        assertTrue(ns.contains(new Position(height/2-1,width-1)));
        assertTrue(ns.contains(new Position(height/2+1,width-1)));
        assertEquals(3, ns.size());

        ns = sc.getHVneighbors(height-1,0);
        assertTrue(ns.contains(new Position(height-2,0)));
        assertTrue(ns.contains(new Position(height-1, 1)));
        assertEquals(2, ns.size());

        ns = sc.getHVneighbors(height-1,width/2);
        assertTrue(ns.contains(new Position(height-2,width/2)));
        assertTrue(ns.contains(new Position(height-1, width/2-1)));
        assertTrue(ns.contains(new Position(height-1, width/2+1)));
        assertEquals(3, ns.size());

        ns = sc.getHVneighbors(height-1,width-1);
        assertTrue(ns.contains(new Position(height-2,width-1)));
        assertTrue(ns.contains(new Position(height-2, width-1)));
        assertEquals(2, ns.size());
    }

    @Test
    public void getBelowNeighbors () {
        ArrayList<Position> ns;

        ns = sc.getBelowNeighbors(0,0);
        assertTrue(ns.contains(new Position(1,0)));
        assertTrue(ns.contains(new Position(1, 1)));
        assertEquals(2, ns.size());

        ns = sc.getBelowNeighbors(0,width/2);
        assertTrue(ns.contains(new Position(1,width/2-1)));
        assertTrue(ns.contains(new Position(1,width/2)));
        assertTrue(ns.contains(new Position(1,width/2+1)));
        assertEquals(3, ns.size());

        ns = sc.getBelowNeighbors(0,width-1);
        assertTrue(ns.contains(new Position(1,width-2)));
        assertTrue(ns.contains(new Position(1,width-1)));
        assertEquals(2, ns.size());

        ns = sc.getBelowNeighbors(height/2,0);
        assertTrue(ns.contains(new Position(height/2+1,0)));
        assertTrue(ns.contains(new Position(height/2+1, 1)));
        assertEquals(2, ns.size());

        ns = sc.getBelowNeighbors(height/2,width/2);
        assertTrue(ns.contains(new Position(height/2+1,width/2-1)));
        assertTrue(ns.contains(new Position(height/2+1,width/2)));
        assertTrue(ns.contains(new Position(height/2+1,width/2+1)));
        assertEquals(3, ns.size());

        ns = sc.getBelowNeighbors(height/2,width-1);
        assertTrue(ns.contains(new Position(height/2+1,width-2)));
        assertTrue(ns.contains(new Position(height/2+1,width-1)));
        assertEquals(2, ns.size());

        for (int w=0; w<width; w++) {
            ns = sc.getBelowNeighbors(height-1,w);
            assertTrue(ns.isEmpty());
        }
    }

    @Test
    public void computeEnergy () {
        assertEquals(20, sc.computeEnergy(28,748));
        assertEquals(84, sc.computeEnergy(59,406));
        assertEquals(39, sc.computeEnergy(92,462));
        assertEquals(0, sc.computeEnergy(101,332));
        assertEquals(96, sc.computeEnergy(237,602));
        assertEquals(7, sc.computeEnergy(387,34));
        assertEquals(0, sc.computeEnergy(394,673));
        assertEquals(6, sc.computeEnergy(397,213));
        assertEquals(84, sc.computeEnergy(442,63));
        assertEquals(16, sc.computeEnergy(510,388));
        assertEquals(0, sc.computeEnergy(535,899));
        assertEquals(27, sc.computeEnergy(546,689));
        assertEquals(26, sc.computeEnergy(599,359));
        assertEquals(23, sc.computeEnergy(629,4));
        assertEquals(0, sc.computeEnergy(673,53));
    }

    @Test
    public void seamSmall () throws IOException {
        SeamCarving sc = new SeamCarving();
        sc.hash.clear();
        sc.readImage("small-line.jpg");
        Pair<List<Position>, Integer> r = sc.findSeam(0,0);
        assertEquals(0, (int) r.getSecond());
        try {
            List<Position> seam = r.getFirst();
            for (int i=0; i<21; i++) {
                assertEquals(new Position(i, 0), seam.getFirst());
                seam = seam.getRest();
            }
        }
        catch (EmptyListE e) {}
    }

    @Test
    public void seamBig () {
        sc.hash.clear();
        Pair<List<Position>, Integer> r = sc.findSeam(height-10,width/2);
        assertEquals(6, (int) r.getSecond());
        try {
            List<Position> seam = r.getFirst();
            assertEquals(new Position(665, 450), seam.getFirst());
            seam = seam.getRest();
            assertEquals(new Position(666, 450), seam.getFirst());
            seam = seam.getRest();
            assertEquals(new Position(667, 450), seam.getFirst());
            seam = seam.getRest();
            assertEquals(new Position(668, 450), seam.getFirst());
            seam = seam.getRest();
            assertEquals(new Position(669, 450), seam.getFirst());
            seam = seam.getRest();
            assertEquals(new Position(670, 450), seam.getFirst());
            seam = seam.getRest();
            assertEquals(new Position(671, 450), seam.getFirst());
            seam = seam.getRest();
            assertEquals(new Position(672, 451), seam.getFirst());
            seam = seam.getRest();
            assertEquals(new Position(673, 451), seam.getFirst());
            seam = seam.getRest();
            assertEquals(new Position(674, 451), seam.getFirst());
            seam = seam.getRest();
        }
        catch (EmptyListE e) {}
    }

    @Test
    //Amr's test example
    public void seam () throws IOException {
        int H = 3; int W = 5;
        int red = Color.red.getRGB();
        int blue = Color.blue.getRGB();
        BufferedImage img = new BufferedImage(W,H,BufferedImage.TYPE_INT_ARGB);
        img.setRGB(0,0,red);
        img.setRGB(1,0,blue);
        img.setRGB(2,0,blue);
        img.setRGB(3,0,blue);
        img.setRGB(4,0,red);
        img.setRGB(0,1,blue);
        img.setRGB(1,1,blue);
        img.setRGB(2,1,blue);
        img.setRGB(3,1,blue);
        img.setRGB(4,1,blue);
        img.setRGB(0,2,red);
        img.setRGB(1,2,blue);
        img.setRGB(2,2,blue);
        img.setRGB(3,2,blue);
        img.setRGB(4,2,red);
        ImageIO.write(img, "png", new File("board.png"));

        SeamCarving board = new SeamCarving();
        board.readImage("board.png");
        Pair<List<Position>,Integer> seamCost;

        seamCost = board.bestSeam();
        assertEquals(0, (int)seamCost.getSecond());
        List<Position> seam = seamCost.getFirst();
        assertEquals(3, seam.length());
        try {
            assertEquals(new Position(0, 2), seam.getFirst());
            seam = seam.getRest();
            assertEquals(new Position(1, 2), seam.getFirst());
            seam = seam.getRest();
            assertEquals(new Position(2, 2), seam.getFirst());
        }
        catch (EmptyListE e) {
            throw new Error("Internal bug");
        }
    }

    @Test
    /*  In this test I draw a 10pixel*10pixel image, the right three columns are blue,
    and the rest area is black and white checkerboard.
    At first the bestseam should be column 8 with energy 0. After a cutseam, the bestseam
    should be the new column 8(which is the previous column9) with energy 0.
    After another cutseam, the bestseam should be the column 7.
    This test verified the code for findseam()(including energy cost test), bestseam(), cutseam() are valid.
    */
    public void smallPixelseam2()throws IOException{
        SeamCarving sc = new SeamCarving();
        sc.readImage("smallpixels2.jpg");
        Pair<List<Position>, Integer> f8 = sc.findSeam(0,8);
        assertEquals(0,(int)f8.getSecond());
        assertEquals("<0,8>,<1,8>,<2,8>,<3,8>,<4,8>,<5,8>,<6,8>,<7,8>,<8,8>,<9,8>,_",f8.getFirst().toString());
        Pair<List<Position>, Integer> f9 = sc.findSeam(0,9);
        assertEquals(0,(int)f9.getSecond());
        assertEquals("<0,9>,<1,9>,<2,9>,<3,9>,<4,9>,<5,9>,<6,9>,<7,9>,<8,9>,<9,9>,_",f9.getFirst().toString());
        Pair<List<Position>, Integer> f6 = sc.findSeam(0,6);
        assertEquals("<0,6>,<1,7>,<2,8>,<3,8>,<4,8>,<5,8>,<6,8>,<7,8>,<8,8>,<9,8>,_",f6.getFirst().toString());
        Pair<List<Position>, Integer> f7 = sc.findSeam(0,7);
        assertEquals("<0,7>,<1,8>,<2,8>,<3,8>,<4,8>,<5,8>,<6,8>,<7,8>,<8,8>,<9,8>,_",f7.getFirst().toString());
        Pair<List<Position>, Integer> r1 = sc.bestSeam();
        assertEquals(0, (int)r1.getSecond());
        assertEquals("<0,8>,<1,8>,<2,8>,<3,8>,<4,8>,<5,8>,<6,8>,<7,8>,<8,8>,<9,8>,_",r1.getFirst().toString());
        sc.cutSeam();
        Pair<List<Position>, Integer> r2 = sc.bestSeam();
        assertEquals(0, (int)r2.getSecond());
        assertEquals("<0,8>,<1,8>,<2,8>,<3,8>,<4,8>,<5,8>,<6,8>,<7,8>,<8,8>,<9,8>,_",r2.getFirst().toString());
        sc.cutSeam();
        Pair<List<Position>, Integer> r3 = sc.bestSeam();
        assertEquals("<0,7>,<1,7>,<2,7>,<3,7>,<4,7>,<5,7>,<6,7>,<7,7>,<8,7>,<9,7>,_",r3.getFirst().toString());
        sc.writeImage("smallpixels2-cut.jpg");
    }

    @Test
    /*  In this test I draw a 10pixel*10pixel image, a zigzag blue line passes through the black and white checkerboard background.
    The blue line should be an seam.
    The bestseam should be the blue line.
    This test verified the code for findseam(), bestseam(), cutseam() are valid.
    */
    public void smallPixelseam1()throws IOException
    {
        SeamCarving sc = new SeamCarving();
        sc.readImage("smallpixels1.jpg");
        Pair<List<Position>, Integer> f = sc.findSeam(0,2);
        assertEquals("<0,2>,<1,2>,<2,3>,<3,4>,<4,4>,<5,3>,<6,2>,<7,3>,<8,4>,<9,5>,_",f.getFirst().toString());
        Pair<List<Position>, Integer> r = sc.bestSeam();
        assertEquals("<0,2>,<1,2>,<2,3>,<3,4>,<4,4>,<5,3>,<6,2>,<7,3>,<8,4>,<9,5>,_",r.getFirst().toString());
        sc.cutSeam();
        sc.writeImage("smallpixels1-cut.jpg");
    }

    @Test
    //two zigzag green lines in a checkerboard background test
    public void seam2()throws IOException{
        int black = Color.black.getRGB();
        int white = Color.white.getRGB();
        int green=Color.green.getRGB();
        BufferedImage image = new BufferedImage(20,10, BufferedImage.TYPE_INT_ARGB);
        //set checkerboard background
        int flag=0;
        for(int h=0;h<10;h++){
            for(int w=0;w<20;w++){
                flag=h%2+w%2;
                if(flag==1){image.setRGB(w,h,black);}
                else{image.setRGB(w,h,white);}
            }
        }
        //draw a green zigzag line
        image.setRGB(5,0,green);
        image.setRGB(6,1,green);
        image.setRGB(7,2,green);
        image.setRGB(8,3,green);
        image.setRGB(9,4,green);
        image.setRGB(10,5,green);
        image.setRGB(9,6,green);
        image.setRGB(8,7,green);
        image.setRGB(7,8,green);
        image.setRGB(6,9,green);
        //draw another green zigzag line
        image.setRGB(18,0,green);
        image.setRGB(17,1,green);
        image.setRGB(18,2,green);
        image.setRGB(17,3,green);
        image.setRGB(16,4,green);
        image.setRGB(15,5,green);
        image.setRGB(14,6,green);
        image.setRGB(13,7,green);
        image.setRGB(14,8,green);
        image.setRGB(15,9,green);
        ImageIO.write(image, "png", new File("2greenlines.png"));
        SeamCarving test = new SeamCarving();
        test.readImage("2greenlines.png");
        //findseam test
        Pair<List<Position>, Integer> s1 = test.findSeam(0,5);
        assertEquals("<0,5>,<1,6>,<2,7>,<3,8>,<4,9>,<5,10>,<6,9>,<7,8>,<8,7>,<9,6>,_",s1.getFirst().toString());
        Pair<List<Position>, Integer> s2 = test.findSeam(6,14);
        assertEquals("<6,14>,<7,13>,<8,14>,<9,15>,_",s2.getFirst().toString());
        //bestseam test
        Pair<List<Position>, Integer> b1 = test.bestSeam();
        assertEquals("<0,18>,<1,17>,<2,18>,<3,17>,<4,16>,<5,15>,<6,14>,<7,13>,<8,14>,<9,15>,_",b1.getFirst().toString());
        test.cutSeam();
        Pair<List<Position>, Integer> b2 = test.bestSeam();
        assertEquals("<0,17>,<1,17>,<2,17>,<3,17>,<4,16>,<5,15>,<6,14>,<7,13>,<8,13>,<9,14>,_",b2.getFirst().toString());
        test.cutSeam();
        Pair<List<Position>, Integer> b3 = test.bestSeam();
        assertEquals("<0,5>,<1,6>,<2,7>,<3,8>,<4,9>,<5,10>,<6,9>,<7,8>,<8,7>,<9,6>,_",b3.getFirst().toString());
    }

    @Test
    public void bestSeam () {
        Pair<List<Position>, Integer> r = sc.bestSeam();
        assertEquals(3051, (int)r.getSecond());
        assertEquals("<0,812>,<1,812>,<2,811>,<3,810>,<4,809>,<5,808>,<6,807>,<7,807>,<8,806>,<9,806>,<10,806>,<11,805>,<12,805>,<13,805>,<14,805>,<15,805>,<16,805>,<17,805>,<18,805>,<19,806>,<20,806>,<21,805>,<22,805>,<23,806>,<24,807>,<25,808>,<26,809>,<27,809>,<28,809>,<29,810>,<30,811>,<31,812>,<32,813>,<33,814>,<34,815>,<35,816>,<36,817>,<37,818>,<38,819>,<39,820>,<40,821>,<41,822>,<42,823>,<43,824>,<44,825>,<45,826>,<46,826>,<47,825>,<48,825>,<49,825>,<50,825>,<51,825>,<52,825>,<53,825>,<54,825>,<55,826>,<56,825>,<57,824>,<58,823>,<59,822>,<60,821>,<61,820>,<62,820>,<63,820>,<64,820>,<65,819>,<66,819>,<67,820>,<68,821>,<69,822>,<70,822>,<71,821>,<72,821>,<73,822>,<74,821>,<75,820>,<76,819>,<77,819>,<78,820>,<79,821>,<80,822>,<81,822>,<82,823>,<83,824>,<84,825>,<85,826>,<86,826>,<87,826>,<88,826>,<89,827>,<90,828>,<91,829>,<92,830>,<93,831>,<94,831>,<95,832>,<96,833>,<97,834>,<98,835>,<99,836>,<100,836>,<101,836>,<102,837>,<103,838>,<104,839>,<105,840>,<106,840>,<107,841>,<108,841>,<109,841>,<110,842>,<111,841>,<112,841>,<113,841>,<114,841>,<115,841>,<116,841>,<117,841>,<118,841>,<119,841>,<120,841>,<121,841>,<122,841>,<123,841>,<124,841>,<125,841>,<126,840>,<127,839>,<128,838>,<129,838>,<130,838>,<131,838>,<132,838>,<133,838>,<134,838>,<135,838>,<136,838>,<137,838>,<138,838>,<139,838>,<140,838>,<141,838>,<142,838>,<143,838>,<144,838>,<145,838>,<146,838>,<147,838>,<148,837>,<149,837>,<150,837>,<151,838>,<152,838>,<153,838>,<154,838>,<155,838>,<156,838>,<157,839>,<158,840>,<159,841>,<160,841>,<161,840>,<162,839>,<163,838>,<164,837>,<165,836>,<166,835>,<167,834>,<168,833>,<169,834>,<170,833>,<171,832>,<172,831>,<173,832>,<174,833>,<175,833>,<176,833>,<177,833>,<178,833>,<179,834>,<180,833>,<181,833>,<182,833>,<183,834>,<184,834>,<185,833>,<186,832>,<187,832>,<188,832>,<189,831>,<190,830>,<191,829>,<192,829>,<193,829>,<194,829>,<195,829>,<196,829>,<197,829>,<198,829>,<199,829>,<200,828>,<201,827>,<202,826>,<203,825>,<204,824>,<205,823>,<206,822>,<207,821>,<208,820>,<209,820>,<210,820>,<211,820>,<212,820>,<213,819>,<214,818>,<215,817>,<216,816>,<217,815>,<218,814>,<219,813>,<220,812>,<221,811>,<222,810>,<223,809>,<224,809>,<225,809>,<226,809>,<227,808>,<228,807>,<229,806>,<230,805>,<231,804>,<232,803>,<233,802>,<234,801>,<235,800>,<236,799>,<237,798>,<238,798>,<239,797>,<240,797>,<241,796>,<242,795>,<243,794>,<244,793>,<245,792>,<246,791>,<247,790>,<248,789>,<249,789>,<250,788>,<251,787>,<252,787>,<253,787>,<254,787>,<255,788>,<256,788>,<257,788>,<258,788>,<259,788>,<260,788>,<261,788>,<262,788>,<263,788>,<264,788>,<265,788>,<266,788>,<267,788>,<268,788>,<269,788>,<270,788>,<271,788>,<272,789>,<273,790>,<274,791>,<275,792>,<276,793>,<277,794>,<278,795>,<279,796>,<280,797>,<281,798>,<282,799>,<283,800>,<284,801>,<285,801>,<286,801>,<287,801>,<288,801>,<289,801>,<290,801>,<291,801>,<292,801>,<293,801>,<294,801>,<295,802>,<296,803>,<297,803>,<298,803>,<299,803>,<300,803>,<301,803>,<302,803>,<303,803>,<304,803>,<305,803>,<306,803>,<307,803>,<308,803>,<309,803>,<310,803>,<311,803>,<312,803>,<313,804>,<314,805>,<315,806>,<316,807>,<317,808>,<318,809>,<319,810>,<320,811>,<321,812>,<322,813>,<323,814>,<324,815>,<325,816>,<326,817>,<327,818>,<328,819>,<329,820>,<330,821>,<331,822>,<332,823>,<333,824>,<334,825>,<335,826>,<336,827>,<337,828>,<338,828>,<339,829>,<340,830>,<341,830>,<342,831>,<343,832>,<344,833>,<345,833>,<346,833>,<347,833>,<348,833>,<349,833>,<350,833>,<351,833>,<352,833>,<353,834>,<354,835>,<355,836>,<356,837>,<357,838>,<358,839>,<359,840>,<360,841>,<361,841>,<362,841>,<363,841>,<364,841>,<365,841>,<366,841>,<367,841>,<368,841>,<369,841>,<370,841>,<371,841>,<372,841>,<373,841>,<374,841>,<375,841>,<376,841>,<377,841>,<378,841>,<379,841>,<380,841>,<381,841>,<382,841>,<383,841>,<384,841>,<385,841>,<386,841>,<387,841>,<388,841>,<389,841>,<390,841>,<391,841>,<392,841>,<393,841>,<394,841>,<395,841>,<396,841>,<397,841>,<398,841>,<399,841>,<400,841>,<401,841>,<402,841>,<403,841>,<404,841>,<405,841>,<406,841>,<407,841>,<408,841>,<409,842>,<410,843>,<411,844>,<412,845>,<413,846>,<414,847>,<415,848>,<416,849>,<417,849>,<418,849>,<419,849>,<420,849>,<421,849>,<422,849>,<423,849>,<424,849>,<425,849>,<426,849>,<427,849>,<428,849>,<429,849>,<430,848>,<431,847>,<432,846>,<433,846>,<434,846>,<435,846>,<436,846>,<437,846>,<438,846>,<439,846>,<440,846>,<441,846>,<442,846>,<443,846>,<444,846>,<445,846>,<446,846>,<447,846>,<448,846>,<449,846>,<450,846>,<451,846>,<452,846>,<453,846>,<454,846>,<455,846>,<456,846>,<457,846>,<458,846>,<459,846>,<460,846>,<461,846>,<462,847>,<463,848>,<464,849>,<465,850>,<466,851>,<467,852>,<468,853>,<469,854>,<470,855>,<471,856>,<472,857>,<473,858>,<474,859>,<475,860>,<476,861>,<477,862>,<478,863>,<479,864>,<480,865>,<481,865>,<482,865>,<483,865>,<484,865>,<485,866>,<486,867>,<487,868>,<488,869>,<489,869>,<490,869>,<491,869>,<492,869>,<493,869>,<494,869>,<495,869>,<496,869>,<497,869>,<498,869>,<499,869>,<500,869>,<501,870>,<502,871>,<503,872>,<504,873>,<505,874>,<506,875>,<507,876>,<508,877>,<509,878>,<510,879>,<511,880>,<512,881>,<513,881>,<514,881>,<515,881>,<516,881>,<517,881>,<518,881>,<519,882>,<520,883>,<521,883>,<522,883>,<523,883>,<524,883>,<525,883>,<526,883>,<527,883>,<528,883>,<529,883>,<530,883>,<531,883>,<532,883>,<533,883>,<534,883>,<535,883>,<536,883>,<537,883>,<538,883>,<539,883>,<540,883>,<541,883>,<542,883>,<543,883>,<544,883>,<545,883>,<546,883>,<547,883>,<548,883>,<549,883>,<550,883>,<551,883>,<552,883>,<553,883>,<554,883>,<555,883>,<556,883>,<557,883>,<558,883>,<559,883>,<560,883>,<561,883>,<562,883>,<563,883>,<564,883>,<565,883>,<566,883>,<567,883>,<568,883>,<569,883>,<570,883>,<571,883>,<572,883>,<573,883>,<574,883>,<575,883>,<576,883>,<577,883>,<578,883>,<579,883>,<580,882>,<581,881>,<582,880>,<583,879>,<584,878>,<585,878>,<586,878>,<587,878>,<588,878>,<589,878>,<590,878>,<591,878>,<592,878>,<593,877>,<594,876>,<595,875>,<596,874>,<597,873>,<598,872>,<599,871>,<600,870>,<601,869>,<602,868>,<603,867>,<604,866>,<605,865>,<606,864>,<607,863>,<608,862>,<609,862>,<610,862>,<611,862>,<612,862>,<613,862>,<614,861>,<615,860>,<616,859>,<617,858>,<618,857>,<619,856>,<620,855>,<621,854>,<622,853>,<623,852>,<624,851>,<625,850>,<626,849>,<627,848>,<628,847>,<629,846>,<630,845>,<631,844>,<632,843>,<633,842>,<634,841>,<635,840>,<636,839>,<637,838>,<638,837>,<639,836>,<640,835>,<641,834>,<642,833>,<643,832>,<644,831>,<645,830>,<646,829>,<647,828>,<648,827>,<649,826>,<650,825>,<651,824>,<652,823>,<653,822>,<654,822>,<655,822>,<656,822>,<657,821>,<658,820>,<659,819>,<660,818>,<661,817>,<662,816>,<663,815>,<664,814>,<665,814>,<666,814>,<667,814>,<668,813>,<669,812>,<670,811>,<671,810>,<672,809>,<673,809>,<674,809>,_",
                r.getFirst().toString());
    }

    @Test
    public void cutSmall () throws IOException {
        SeamCarving sc = new SeamCarving();
        sc.readImage("small-line.jpg");
        int height = sc.getHeight();
        int width = sc.getWidth();
        assertEquals(height*width,sc.getPixels().length);
        sc.cutSeam();
        assertEquals(height*(width-1),sc.getPixels().length);
    }

    @Test
    public void cutBigBalloonTWoThirds () throws IOException {
        SeamCarving sc = new SeamCarving();
        sc.readImage("balloon-sky.jpg");
        int bound = sc.getWidth() * 2 / 3;
        for (int i=0; i<bound; i++) {
            System.out.printf("%d of %d%n", i+1, bound);
            sc.cutSeam();
        }
        sc.writeImage("balloon-sky-cut.jpg");
    }

    @Test
    public void cutBigSunThird () throws IOException {
        SeamCarving sc = new SeamCarving();
        sc.readImage("winter-sun.jpg");
        int bound = sc.getWidth() * 1 / 3;
        for (int i=0; i<bound; i++) {
            System.out.printf("%d of %d%n", i+1, bound);
            sc.cutSeam();
        }
        sc.writeImage("winter-sun-cut.jpg");
    }

    @Test
    public void cutBigSunHalf () throws IOException {
        SeamCarving sc = new SeamCarving();
        sc.readImage("winter-sun.jpg");
        int bound = sc.getWidth() * 1 / 2;
        for (int i=0; i<bound; i++) {
            System.out.printf("%d of %d%n", i+1, bound);
            sc.cutSeam();
        }
        sc.writeImage("winter-sun-cut2.jpg");
    }

    //Below are my personal trials for seamcarving on different kinds of images.

    @Test
    public void cutSnoopy1TwoThirds () throws IOException {
        SeamCarving sc = new SeamCarving();
        sc.readImage("snoopy1.jpeg");
        int bound = sc.getWidth() * 2 / 3;
        for (int i=0; i<bound; i++) {
            System.out.printf("%d of %d%n", i+1, bound);
            sc.cutSeam();
        }
        sc.writeImage("snoopy1-cut.jpeg");
    }

    @Test
    public void cutSnoopy2AThird () throws IOException {
        SeamCarving sc = new SeamCarving();
        sc.readImage("snoopy2.jpg");
        int bound = sc.getWidth() * 1 / 3;
        for (int i=0; i<bound; i++) {
            System.out.printf("%d of %d%n", i+1, bound);
            sc.cutSeam();
        }
        sc.writeImage("snoopy2-cut.jpg");
    }

    @Test
    public void cutIUplaidAQuarter () throws IOException {
        SeamCarving sc = new SeamCarving();
        sc.readImage("iuplaid.jpg");
        int bound = sc.getWidth() * 1 / 4;
        for (int i=0; i<bound; i++) {
            System.out.printf("%d of %d%n", i+1, bound);
            sc.cutSeam();
        }
        sc.writeImage("iuplaid-cut.jpg");
    }

    @Test
    public void cutStarAHalf () throws IOException {
        SeamCarving sc = new SeamCarving();
        sc.readImage("star.jpg");
        int bound = sc.getWidth() * 1 / 2;
        for (int i=0; i<bound; i++) {
            System.out.printf("%d of %d%n", i+1, bound);
            sc.cutSeam();
        }
        sc.writeImage("star-cut.jpg");
    }

    @Test
    public void cutVanGoghAThird () throws IOException {
        SeamCarving sc = new SeamCarving();
        sc.readImage("vangogh.jpg");
        int bound = sc.getWidth() * 1 / 3;
        for (int i=0; i<bound; i++) {
            System.out.printf("%d of %d%n", i+1, bound);
            sc.cutSeam();
        }
        sc.writeImage("vangogh-cut.jpg");
    }

    @Test
    public void cutMatisse1AThird () throws IOException {
        SeamCarving sc = new SeamCarving();
        sc.readImage("matisse1.jpg");
        int bound = sc.getWidth() * 1 / 3;
        for (int i=0; i<bound; i++) {
            System.out.printf("%d of %d%n", i+1, bound);
            sc.cutSeam();
        }
        sc.writeImage("matisse1-cut.jpg");
    }

    @Test
    public void cutMatisse2AThird () throws IOException {
        SeamCarving sc = new SeamCarving();
        sc.readImage("matisse2.jpg");
        int bound = sc.getWidth() * 1 / 3;
        for (int i=0; i<bound; i++) {
            System.out.printf("%d of %d%n", i+1, bound);
            sc.cutSeam();
        }
        sc.writeImage("matisse2-cut.jpg");
    }

    @Test
    public void cutAkimotoThreeThirds () throws IOException {
        SeamCarving sc = new SeamCarving();
        sc.readImage("akimoto.jpg");
        int bound = sc.getWidth() * 3 / 5;
        for (int i=0; i<bound; i++) {
            System.out.printf("%d of %d%n", i+1, bound);
            sc.cutSeam();
        }
        sc.writeImage("akimoto-cut.jpg");
    }
    
}