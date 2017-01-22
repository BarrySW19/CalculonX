/**
 * Calculon - A Java chess-engine.
 *
 * Copyright (C) 2008-2009 Barry Smith
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package barrysw19.calculon.engine;

/**
 * All in the name of performance... lots and lots of magic numbers.
 * 
 */
public class Bitmaps {

	public static final short BM_U 	= 0;
	public static final short BM_UR	= 1;
	public static final short BM_R 	= 2;
	public static final short BM_DR	= 3;
	public static final short BM_D 	= 4;
	public static final short BM_DL	= 5;
	public static final short BM_L 	= 6;
	public static final short BM_UL	= 7;

    // Mappings of co-ord differences to direction
	public static final int[][] DIR_MAP = new int[][] {
			{  BM_UR, BM_R, BM_DR },
			{  BM_U,  -99,  BM_D },
			{  BM_UL, BM_L, BM_DL }
	};

	public static final long BORDER = -35604928818740737L;
	
	public static final long[][] maps2 = new long[8][64];
	public static final long[] cross2Map = new long[64];
	public static final long[] diag2Map = new long[64];
	public static final long[] star2Map = new long[64];

	/** Bitmaps of all the squares between pieces on the two specified indexes */
	public static final long[][] SLIDE_MOVES = generateSlideMoves();

    private static long[][] generateSlideMoves() {
        final long[][] moves = new long[64][64];
        for(int i = 0; i < 64; i++) {
            for(int j = 0; j < 64; j++) {
                if(i == j) {
                    continue;
                }

                int[] from = new int[] { Math.min(i, j) >>> 3, Math.min(i, j) & 0x07 };
                int[] to = new int[] { Math.max(i, j) >>> 3, Math.max(i, j) & 0x07 };

                if(from[0] == to[0]) {
                    int p = from[1] + 1;
                    while(p < to[1]) {
                        moves[i][j] |= (1L<<(from[0]<<3))<<p;
                        p++;
                    }
                }

                if(from[1] == to[1]) {
                    int p = from[0] + 1;
                    while(p < to[0]) {
                        moves[i][j] |= (1L<<(p<<3))<<from[1];
                        p++;
                    }
                }

                if(Math.abs(from[0] - to[0]) == Math.abs(from[1] - to[1])) {
                    int p = from[0] + 1;
                    int dir = (from[1] < to[1]) ? 1 : -1;
                    while(p < to[0]) {
                        long rBit = (1L<<(p<<3))<<from[1];
                        moves[i][j] |= dir > 0 ? rBit<<dir : rBit>>>Math.abs(dir);
                        dir += Math.signum(dir);
                        p++;
                    }
                }
            }
        }
        return moves;
    }

    static {
		maps2[BM_UL][0] = 0L;
		maps2[BM_DL][0] = 0L;
		maps2[BM_U][0] = 72340172838076672L;
		maps2[BM_UR][0] = -9205322385119247872L;
		maps2[BM_R][0] = 254L;
		maps2[BM_L][0] = 0L;
		maps2[BM_D][0] = 0L;
		maps2[BM_DR][0] = 0L;
		maps2[BM_UL][8] = 0L;
		maps2[BM_DL][8] = 0L;
		maps2[BM_U][8] = 72340172838076416L;
		maps2[BM_UR][8] = 4620710844295151616L;
		maps2[BM_R][8] = 65024L;
		maps2[BM_L][8] = 0L;
		maps2[BM_D][8] = 1L;
		maps2[BM_DR][8] = 2L;
		maps2[BM_UL][16] = 0L;
		maps2[BM_DL][16] = 0L;
		maps2[BM_U][16] = 72340172838010880L;
		maps2[BM_UR][16] = 2310355422147510272L;
		maps2[BM_R][16] = 16646144L;
		maps2[BM_L][16] = 0L;
		maps2[BM_D][16] = 257L;
		maps2[BM_DR][16] = 516L;
		maps2[BM_UL][24] = 0L;
		maps2[BM_DL][24] = 0L;
		maps2[BM_U][24] = 72340172821233664L;
		maps2[BM_UR][24] = 1155177711056977920L;
		maps2[BM_R][24] = 4261412864L;
		maps2[BM_L][24] = 0L;
		maps2[BM_D][24] = 65793L;
		maps2[BM_DR][24] = 132104L;
		maps2[BM_UL][32] = 0L;
		maps2[BM_DL][32] = 0L;
		maps2[BM_U][32] = 72340168526266368L;
		maps2[BM_UR][32] = 577588851233521664L;
		maps2[BM_R][32] = 1090921693184L;
		maps2[BM_L][32] = 0L;
		maps2[BM_D][32] = 16843009L;
		maps2[BM_DR][32] = 33818640L;
		maps2[BM_UL][40] = 0L;
		maps2[BM_DL][40] = 0L;
		maps2[BM_U][40] = 72339069014638592L;
		maps2[BM_UR][40] = 288793326105133056L;
		maps2[BM_R][40] = 279275953455104L;
		maps2[BM_L][40] = 0L;
		maps2[BM_D][40] = 4311810305L;
		maps2[BM_DR][40] = 8657571872L;
		maps2[BM_UL][48] = 0L;
		maps2[BM_DL][48] = 0L;
		maps2[BM_U][48] = 72057594037927936L;
		maps2[BM_UR][48] = 144115188075855872L;
		maps2[BM_R][48] = 71494644084506624L;
		maps2[BM_L][48] = 0L;
		maps2[BM_D][48] = 1103823438081L;
		maps2[BM_DR][48] = 2216338399296L;
		maps2[BM_UL][56] = 0L;
		maps2[BM_DL][56] = 0L;
		maps2[BM_U][56] = 0L;
		maps2[BM_UR][56] = 0L;
		maps2[BM_R][56] = -144115188075855872L;
		maps2[BM_L][56] = 0L;
		maps2[BM_D][56] = 282578800148737L;
		maps2[BM_DR][56] = 567382630219904L;
		maps2[BM_UL][1] = 256L;
		maps2[BM_DL][1] = 0L;
		maps2[BM_U][1] = 144680345676153344L;
		maps2[BM_UR][1] = 36099303471055872L;
		maps2[BM_R][1] = 252L;
		maps2[BM_L][1] = 1L;
		maps2[BM_D][1] = 0L;
		maps2[BM_DR][1] = 0L;
		maps2[BM_UL][9] = 65536L;
		maps2[BM_DL][9] = 1L;
		maps2[BM_U][9] = 144680345676152832L;
		maps2[BM_UR][9] = -9205322385119248384L;
		maps2[BM_R][9] = 64512L;
		maps2[BM_L][9] = 256L;
		maps2[BM_D][9] = 2L;
		maps2[BM_DR][9] = 4L;
		maps2[BM_UL][17] = 16777216L;
		maps2[BM_DL][17] = 256L;
		maps2[BM_U][17] = 144680345676021760L;
		maps2[BM_UR][17] = 4620710844295020544L;
		maps2[BM_R][17] = 16515072L;
		maps2[BM_L][17] = 65536L;
		maps2[BM_D][17] = 514L;
		maps2[BM_DR][17] = 1032L;
		maps2[BM_UL][25] = 4294967296L;
		maps2[BM_DL][25] = 65536L;
		maps2[BM_U][25] = 144680345642467328L;
		maps2[BM_UR][25] = 2310355422113955840L;
		maps2[BM_R][25] = 4227858432L;
		maps2[BM_L][25] = 16777216L;
		maps2[BM_D][25] = 131586L;
		maps2[BM_DR][25] = 264208L;
		maps2[BM_UL][33] = 1099511627776L;
		maps2[BM_DL][33] = 16777216L;
		maps2[BM_U][33] = 144680337052532736L;
		maps2[BM_UR][33] = 1155177702467043328L;
		maps2[BM_R][33] = 1082331758592L;
		maps2[BM_L][33] = 4294967296L;
		maps2[BM_D][33] = 33686018L;
		maps2[BM_DR][33] = 67637280L;
		maps2[BM_UL][41] = 281474976710656L;
		maps2[BM_DL][41] = 4294967296L;
		maps2[BM_U][41] = 144678138029277184L;
		maps2[BM_UR][41] = 577586652210266112L;
		maps2[BM_R][41] = 277076930199552L;
		maps2[BM_L][41] = 1099511627776L;
		maps2[BM_D][41] = 8623620610L;
		maps2[BM_DR][41] = 17315143744L;
		maps2[BM_UL][49] = 72057594037927936L;
		maps2[BM_DL][49] = 1099511627776L;
		maps2[BM_U][49] = 144115188075855872L;
		maps2[BM_UR][49] = 288230376151711744L;
		maps2[BM_R][49] = 70931694131085312L;
		maps2[BM_L][49] = 281474976710656L;
		maps2[BM_D][49] = 2207646876162L;
		maps2[BM_DR][49] = 4432676798592L;
		maps2[BM_UL][57] = 0L;
		maps2[BM_DL][57] = 281474976710656L;
		maps2[BM_U][57] = 0L;
		maps2[BM_UR][57] = 0L;
		maps2[BM_R][57] = -288230376151711744L;
		maps2[BM_L][57] = 72057594037927936L;
		maps2[BM_D][57] = 565157600297474L;
		maps2[BM_DR][57] = 1134765260439552L;
		maps2[BM_UL][2] = 66048L;
		maps2[BM_DL][2] = 0L;
		maps2[BM_U][2] = 289360691352306688L;
		maps2[BM_UR][2] = 141012904183808L;
		maps2[BM_R][2] = 248L;
		maps2[BM_L][2] = 3L;
		maps2[BM_D][2] = 0L;
		maps2[BM_DR][2] = 0L;
		maps2[BM_UL][10] = 16908288L;
		maps2[BM_DL][10] = 2L;
		maps2[BM_U][10] = 289360691352305664L;
		maps2[BM_UR][10] = 36099303471054848L;
		maps2[BM_R][10] = 63488L;
		maps2[BM_L][10] = 768L;
		maps2[BM_D][10] = 4L;
		maps2[BM_DR][10] = 8L;
		maps2[BM_UL][18] = 4328521728L;
		maps2[BM_DL][18] = 513L;
		maps2[BM_U][18] = 289360691352043520L;
		maps2[BM_UR][18] = -9205322385119510528L;
		maps2[BM_R][18] = 16252928L;
		maps2[BM_L][18] = 196608L;
		maps2[BM_D][18] = 1028L;
		maps2[BM_DR][18] = 2064L;
		maps2[BM_UL][26] = 1108101562368L;
		maps2[BM_DL][26] = 131328L;
		maps2[BM_U][26] = 289360691284934656L;
		maps2[BM_UR][26] = 4620710844227911680L;
		maps2[BM_R][26] = 4160749568L;
		maps2[BM_L][26] = 50331648L;
		maps2[BM_D][26] = 263172L;
		maps2[BM_DR][26] = 528416L;
		maps2[BM_UL][34] = 283673999966208L;
		maps2[BM_DL][34] = 33619968L;
		maps2[BM_U][34] = 289360674105065472L;
		maps2[BM_UR][34] = 2310355404934086656L;
		maps2[BM_R][34] = 1065151889408L;
		maps2[BM_L][34] = 12884901888L;
		maps2[BM_D][34] = 67372036L;
		maps2[BM_DR][34] = 135274560L;
		maps2[BM_UL][42] = 72620543991349248L;
		maps2[BM_DL][42] = 8606711808L;
		maps2[BM_U][42] = 289356276058554368L;
		maps2[BM_UR][42] = 1155173304420532224L;
		maps2[BM_R][42] = 272678883688448L;
		maps2[BM_L][42] = 3298534883328L;
		maps2[BM_D][42] = 17247241220L;
		maps2[BM_DR][42] = 34630287488L;
		maps2[BM_UL][50] = 144115188075855872L;
		maps2[BM_DL][50] = 2203318222848L;
		maps2[BM_U][50] = 288230376151711744L;
		maps2[BM_UR][50] = 576460752303423488L;
		maps2[BM_R][50] = 69805794224242688L;
		maps2[BM_L][50] = 844424930131968L;
		maps2[BM_D][50] = 4415293752324L;
		maps2[BM_DR][50] = 8865353596928L;
		maps2[BM_UL][58] = 0L;
		maps2[BM_DL][58] = 564049465049088L;
		maps2[BM_U][58] = 0L;
		maps2[BM_UR][58] = 0L;
		maps2[BM_R][58] = -576460752303423488L;
		maps2[BM_L][58] = 216172782113783808L;
		maps2[BM_D][58] = 1130315200594948L;
		maps2[BM_DR][58] = 2269530520813568L;
		maps2[BM_UL][3] = 16909312L;
		maps2[BM_DL][3] = 0L;
		maps2[BM_U][3] = 578721382704613376L;
		maps2[BM_UR][3] = 550831656960L;
		maps2[BM_R][3] = 240L;
		maps2[BM_L][3] = 7L;
		maps2[BM_D][3] = 0L;
		maps2[BM_DR][3] = 0L;
		maps2[BM_UL][11] = 4328783872L;
		maps2[BM_DL][11] = 4L;
		maps2[BM_U][11] = 578721382704611328L;
		maps2[BM_UR][11] = 141012904181760L;
		maps2[BM_R][11] = 61440L;
		maps2[BM_L][11] = 1792L;
		maps2[BM_D][11] = 8L;
		maps2[BM_DR][11] = 16L;
		maps2[BM_UL][19] = 1108168671232L;
		maps2[BM_DL][19] = 1026L;
		maps2[BM_U][19] = 578721382704087040L;
		maps2[BM_UR][19] = 36099303470530560L;
		maps2[BM_R][19] = 15728640L;
		maps2[BM_L][19] = 458752L;
		maps2[BM_D][19] = 2056L;
		maps2[BM_DR][19] = 4128L;
		maps2[BM_UL][27] = 283691179835392L;
		maps2[BM_DL][27] = 262657L;
		maps2[BM_U][27] = 578721382569869312L;
		maps2[BM_UR][27] = -9205322385253728256L;
		maps2[BM_R][27] = 4026531840L;
		maps2[BM_L][27] = 117440512L;
		maps2[BM_D][27] = 526344L;
		maps2[BM_DR][27] = 1056832L;
		maps2[BM_UL][35] = 72624942037860352L;
		maps2[BM_DL][35] = 67240192L;
		maps2[BM_U][35] = 578721348210130944L;
		maps2[BM_UR][35] = 4620710809868173312L;
		maps2[BM_R][35] = 1030792151040L;
		maps2[BM_L][35] = 30064771072L;
		maps2[BM_D][35] = 134744072L;
		maps2[BM_DR][35] = 270549120L;
		maps2[BM_UL][43] = 145241087982698496L;
		maps2[BM_DL][43] = 17213489152L;
		maps2[BM_U][43] = 578712552117108736L;
		maps2[BM_UR][43] = 2310346608841064448L;
		maps2[BM_R][43] = 263882790666240L;
		maps2[BM_L][43] = 7696581394432L;
		maps2[BM_D][43] = 34494482440L;
		maps2[BM_DR][43] = 69260574720L;
		maps2[BM_UL][51] = 288230376151711744L;
		maps2[BM_DL][51] = 4406653222912L;
		maps2[BM_U][51] = 576460752303423488L;
		maps2[BM_UR][51] = 1152921504606846976L;
		maps2[BM_R][51] = 67553994410557440L;
		maps2[BM_L][51] = 1970324836974592L;
		maps2[BM_D][51] = 8830587504648L;
		maps2[BM_DR][51] = 17730707128320L;
		maps2[BM_UL][59] = 0L;
		maps2[BM_DL][59] = 1128103225065472L;
		maps2[BM_U][59] = 0L;
		maps2[BM_UR][59] = 0L;
		maps2[BM_R][59] = -1152921504606846976L;
		maps2[BM_L][59] = 504403158265495552L;
		maps2[BM_D][59] = 2260630401189896L;
		maps2[BM_DR][59] = 4539061024849920L;
		maps2[BM_UL][4] = 4328785920L;
		maps2[BM_DL][4] = 0L;
		maps2[BM_U][4] = 1157442765409226752L;
		maps2[BM_UR][4] = 2151686144L;
		maps2[BM_R][4] = 224L;
		maps2[BM_L][4] = 15L;
		maps2[BM_D][4] = 0L;
		maps2[BM_DR][4] = 0L;
		maps2[BM_UL][12] = 1108169195520L;
		maps2[BM_DL][12] = 8L;
		maps2[BM_U][12] = 1157442765409222656L;
		maps2[BM_UR][12] = 550831652864L;
		maps2[BM_R][12] = 57344L;
		maps2[BM_L][12] = 3840L;
		maps2[BM_D][12] = 16L;
		maps2[BM_DR][12] = 32L;
		maps2[BM_UL][20] = 283691314053120L;
		maps2[BM_DL][20] = 2052L;
		maps2[BM_U][20] = 1157442765408174080L;
		maps2[BM_UR][20] = 141012903133184L;
		maps2[BM_R][20] = 14680064L;
		maps2[BM_L][20] = 983040L;
		maps2[BM_D][20] = 4112L;
		maps2[BM_DR][20] = 8256L;
		maps2[BM_UL][28] = 72624976397598720L;
		maps2[BM_DL][28] = 525314L;
		maps2[BM_U][28] = 1157442765139738624L;
		maps2[BM_UR][28] = 36099303202095104L;
		maps2[BM_R][28] = 3758096384L;
		maps2[BM_L][28] = 251658240L;
		maps2[BM_D][28] = 1052688L;
		maps2[BM_DR][28] = 2113664L;
		maps2[BM_UL][36] = 145249884075720704L;
		maps2[BM_DL][36] = 134480385L;
		maps2[BM_U][36] = 1157442696420261888L;
		maps2[BM_UR][36] = -9205322453973204992L;
		maps2[BM_R][36] = 962072674304L;
		maps2[BM_L][36] = 64424509440L;
		maps2[BM_D][36] = 269488144L;
		maps2[BM_DR][36] = 541097984L;
		maps2[BM_UL][44] = 290482175965396992L;
		maps2[BM_DL][44] = 34426978560L;
		maps2[BM_U][44] = 1157425104234217472L;
		maps2[BM_UR][44] = 4620693217682128896L;
		maps2[BM_R][44] = 246290604621824L;
		maps2[BM_L][44] = 16492674416640L;
		maps2[BM_D][44] = 68988964880L;
		maps2[BM_DR][44] = 138521083904L;
		maps2[BM_UL][52] = 576460752303423488L;
		maps2[BM_DL][52] = 8813306511360L;
		maps2[BM_U][52] = 1152921504606846976L;
		maps2[BM_UR][52] = 2305843009213693952L;
		maps2[BM_R][52] = 63050394783186944L;
		maps2[BM_L][52] = 4222124650659840L;
		maps2[BM_D][52] = 17661175009296L;
		maps2[BM_DR][52] = 35461397479424L;
		maps2[BM_UL][60] = 0L;
		maps2[BM_DL][60] = 2256206466908160L;
		maps2[BM_U][60] = 0L;
		maps2[BM_UR][60] = 0L;
		maps2[BM_R][60] = -2305843009213693952L;
		maps2[BM_L][60] = 1080863910568919040L;
		maps2[BM_D][60] = 4521260802379792L;
		maps2[BM_DR][60] = 9078117754732544L;
		maps2[BM_UL][5] = 1108169199616L;
		maps2[BM_DL][5] = 0L;
		maps2[BM_U][5] = 2314885530818453504L;
		maps2[BM_UR][5] = 8404992L;
		maps2[BM_R][5] = 192L;
		maps2[BM_L][5] = 31L;
		maps2[BM_D][5] = 0L;
		maps2[BM_DR][5] = 0L;
		maps2[BM_UL][13] = 283691315101696L;
		maps2[BM_DL][13] = 16L;
		maps2[BM_U][13] = 2314885530818445312L;
		maps2[BM_UR][13] = 2151677952L;
		maps2[BM_R][13] = 49152L;
		maps2[BM_L][13] = 7936L;
		maps2[BM_D][13] = 32L;
		maps2[BM_DR][13] = 64L;
		maps2[BM_UL][21] = 72624976666034176L;
		maps2[BM_DL][21] = 4104L;
		maps2[BM_U][21] = 2314885530816348160L;
		maps2[BM_UR][21] = 550829555712L;
		maps2[BM_R][21] = 12582912L;
		maps2[BM_L][21] = 2031616L;
		maps2[BM_D][21] = 8224L;
		maps2[BM_DR][21] = 16512L;
		maps2[BM_UL][29] = 145249952795197440L;
		maps2[BM_DL][29] = 1050628L;
		maps2[BM_U][29] = 2314885530279477248L;
		maps2[BM_UR][29] = 141012366262272L;
		maps2[BM_R][29] = 3221225472L;
		maps2[BM_L][29] = 520093696L;
		maps2[BM_D][29] = 2105376L;
		maps2[BM_DR][29] = 4227072L;
		maps2[BM_UL][37] = 290499768151441408L;
		maps2[BM_DL][37] = 268960770L;
		maps2[BM_U][37] = 2314885392840523776L;
		maps2[BM_UR][37] = 36099165763141632L;
		maps2[BM_R][37] = 824633720832L;
		maps2[BM_L][37] = 133143986176L;
		maps2[BM_D][37] = 538976288L;
		maps2[BM_DR][37] = 1082130432L;
		maps2[BM_UL][45] = 580964351930793984L;
		maps2[BM_DL][45] = 68853957121L;
		maps2[BM_U][45] = 2314850208468434944L;
		maps2[BM_UR][45] = -9205357638345293824L;
		maps2[BM_R][45] = 211106232532992L;
		maps2[BM_L][45] = 34084860461056L;
		maps2[BM_D][45] = 137977929760L;
		maps2[BM_DR][45] = 277025390592L;
		maps2[BM_UL][53] = 1152921504606846976L;
		maps2[BM_DL][53] = 17626613022976L;
		maps2[BM_U][53] = 2305843009213693952L;
		maps2[BM_UR][53] = 4611686018427387904L;
		maps2[BM_R][53] = 54043195528445952L;
		maps2[BM_L][53] = 8725724278030336L;
		maps2[BM_D][53] = 35322350018592L;
		maps2[BM_DR][53] = 70918499991552L;
		maps2[BM_UL][61] = 0L;
		maps2[BM_DL][61] = 4512412933881856L;
		maps2[BM_U][61] = 0L;
		maps2[BM_UR][61] = 0L;
		maps2[BM_R][61] = -4611686018427387904L;
		maps2[BM_L][61] = 2233785415175766016L;
		maps2[BM_D][61] = 9042521604759584L;
		maps2[BM_DR][61] = 18155135997837312L;
		maps2[BM_UL][6] = 283691315109888L;
		maps2[BM_DL][6] = 0L;
		maps2[BM_U][6] = 4629771061636907008L;
		maps2[BM_UR][6] = 32768L;
		maps2[BM_R][6] = 128L;
		maps2[BM_L][6] = 63L;
		maps2[BM_D][6] = 0L;
		maps2[BM_DR][6] = 0L;
		maps2[BM_UL][14] = 72624976668131328L;
		maps2[BM_DL][14] = 32L;
		maps2[BM_U][14] = 4629771061636890624L;
		maps2[BM_UR][14] = 8388608L;
		maps2[BM_R][14] = 32768L;
		maps2[BM_L][14] = 16128L;
		maps2[BM_D][14] = 64L;
		maps2[BM_DR][14] = 128L;
		maps2[BM_UL][22] = 145249953332068352L;
		maps2[BM_DL][22] = 8208L;
		maps2[BM_U][22] = 4629771061632696320L;
		maps2[BM_UR][22] = 2147483648L;
		maps2[BM_R][22] = 8388608L;
		maps2[BM_L][22] = 4128768L;
		maps2[BM_D][22] = 16448L;
		maps2[BM_DR][22] = 32768L;
		maps2[BM_UL][30] = 290499905590394880L;
		maps2[BM_DL][30] = 2101256L;
		maps2[BM_U][30] = 4629771060558954496L;
		maps2[BM_UR][30] = 549755813888L;
		maps2[BM_R][30] = 2147483648L;
		maps2[BM_L][30] = 1056964608L;
		maps2[BM_D][30] = 4210752L;
		maps2[BM_DR][30] = 8388608L;
		maps2[BM_UL][38] = 580999536302882816L;
		maps2[BM_DL][38] = 537921540L;
		maps2[BM_U][38] = 4629770785681047552L;
		maps2[BM_UR][38] = 140737488355328L;
		maps2[BM_R][38] = 549755813888L;
		maps2[BM_L][38] = 270582939648L;
		maps2[BM_D][38] = 1077952576L;
		maps2[BM_DR][38] = 2147483648L;
		maps2[BM_UL][46] = 1161928703861587968L;
		maps2[BM_DL][46] = 137707914242L;
		maps2[BM_U][46] = 4629700416936869888L;
		maps2[BM_UR][46] = 36028797018963968L;
		maps2[BM_R][46] = 140737488355328L;
		maps2[BM_L][46] = 69269232549888L;
		maps2[BM_D][46] = 275955859520L;
		maps2[BM_DR][46] = 549755813888L;
		maps2[BM_UL][54] = 2305843009213693952L;
		maps2[BM_DL][54] = 35253226045953L;
		maps2[BM_U][54] = 4611686018427387904L;
		maps2[BM_UR][54] = -9223372036854775808L;
		maps2[BM_R][54] = 36028797018963968L;
		maps2[BM_L][54] = 17732923532771328L;
		maps2[BM_D][54] = 70644700037184L;
		maps2[BM_DR][54] = 140737488355328L;
		maps2[BM_UL][62] = 0L;
		maps2[BM_DL][62] = 9024825867763968L;
		maps2[BM_U][62] = 0L;
		maps2[BM_UR][62] = 0L;
		maps2[BM_R][62] = -9223372036854775808L;
		maps2[BM_L][62] = 4539628424389459968L;
		maps2[BM_D][62] = 18085043209519168L;
		maps2[BM_DR][62] = 36028797018963968L;
		maps2[BM_UL][7] = 72624976668147712L;
		maps2[BM_DL][7] = 0L;
		maps2[BM_U][7] = -9187201950435737600L;
		maps2[BM_UR][7] = 0L;
		maps2[BM_R][7] = 0L;
		maps2[BM_L][7] = 127L;
		maps2[BM_D][7] = 0L;
		maps2[BM_DR][7] = 0L;
		maps2[BM_UL][15] = 145249953336262656L;
		maps2[BM_DL][15] = 64L;
		maps2[BM_U][15] = -9187201950435770368L;
		maps2[BM_UR][15] = 0L;
		maps2[BM_R][15] = 0L;
		maps2[BM_L][15] = 32512L;
		maps2[BM_D][15] = 128L;
		maps2[BM_DR][15] = 0L;
		maps2[BM_UL][23] = 290499906664136704L;
		maps2[BM_DL][23] = 16416L;
		maps2[BM_U][23] = -9187201950444158976L;
		maps2[BM_UR][23] = 0L;
		maps2[BM_R][23] = 0L;
		maps2[BM_L][23] = 8323072L;
		maps2[BM_D][23] = 32896L;
		maps2[BM_DR][23] = 0L;
		maps2[BM_UL][31] = 580999811180789760L;
		maps2[BM_DL][31] = 4202512L;
		maps2[BM_U][31] = -9187201952591642624L;
		maps2[BM_UR][31] = 0L;
		maps2[BM_R][31] = 0L;
		maps2[BM_L][31] = 2130706432L;
		maps2[BM_D][31] = 8421504L;
		maps2[BM_DR][31] = 0L;
		maps2[BM_UL][39] = 1161999072605765632L;
		maps2[BM_DL][39] = 1075843080L;
		maps2[BM_U][39] = -9187202502347456512L;
		maps2[BM_UR][39] = 0L;
		maps2[BM_R][39] = 0L;
		maps2[BM_L][39] = 545460846592L;
		maps2[BM_D][39] = 2155905152L;
		maps2[BM_DR][39] = 0L;
		maps2[BM_UL][47] = 2323857407723175936L;
		maps2[BM_DL][47] = 275415828484L;
		maps2[BM_U][47] = -9187343239835811840L;
		maps2[BM_UR][47] = 0L;
		maps2[BM_R][47] = 0L;
		maps2[BM_L][47] = 139637976727552L;
		maps2[BM_D][47] = 551911719040L;
		maps2[BM_DR][47] = 0L;
		maps2[BM_UL][55] = 4611686018427387904L;
		maps2[BM_DL][55] = 70506452091906L;
		maps2[BM_U][55] = -9223372036854775808L;
		maps2[BM_UR][55] = 0L;
		maps2[BM_R][55] = 0L;
		maps2[BM_L][55] = 35747322042253312L;
		maps2[BM_D][55] = 141289400074368L;
		maps2[BM_DR][55] = 0L;
		maps2[BM_UL][63] = 0L;
		maps2[BM_DL][63] = 18049651735527937L;
		maps2[BM_U][63] = 0L;
		maps2[BM_UR][63] = 0L;
		maps2[BM_R][63] = 0L;
		maps2[BM_L][63] = 9151314442816847872L;
		maps2[BM_D][63] = 36170086419038336L;
		maps2[BM_DR][63] = 0L;

		for(int i = 0; i < 64; i++) {
			cross2Map[i] = (maps2[BM_U][i] | maps2[BM_D][i] | maps2[BM_L][i] | maps2[BM_R][i]);
			diag2Map[i] = (maps2[BM_UR][i] | maps2[BM_DR][i] | maps2[BM_UL][i] | maps2[BM_DL][i]);
			star2Map[i] = (cross2Map[i] | diag2Map[i]);
		}
	}
}
