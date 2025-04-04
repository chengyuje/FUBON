--**************************************************************************
--PROJECT_CODE> TBPMS
--DATE>2016/10/17
--AUTHOR> Grace Liu
--PURPOSE>ST Load
--**************************************************************************

--**************************************************************************
--TARGET>TBPMS_TR1FIL_SG
--TRUNCATE_BEFORE_INSERT>Y
--**************************************************************************
--OPTIONS (ERRORS = 0)
OPTIONS (direct=true)
LOAD DATA
CHARACTERSET AL32UTF8
TRUNCATE
into table	TBPMS_TR1FIL_SG
--FIELDS TERMINATED BY X'09'
FIELDS TERMINATED BY ',' --OPTIONALLY ENCLOSED BY '"'
TRAILING NULLCOLS
(
TR101 POSITION(1:8) 
,TR102 POSITION(9:19)
,TR103 POSITION(20:21)
,TR104 POSITION(22:25)
,TR107 POSITION(26:34)
,TR105 POSITION(35:38)
,TR137 POSITION(39:47)
,TR106 POSITION(48:48)
,TR108 POSITION(49:59)
,TR109 POSITION(60:67)
,TR110 POSITION(68:69) 
,TR111 POSITION(70:78) 
,TR112 POSITION(79:87)
,TR113 POSITION(88:96) "TO_NUMBER(:TR113/10000)"
,TR114 POSITION(97:97)
,TR115 POSITION(98:109) "TO_NUMBER(:TR115/10000)"
,TR116 POSITION(110:119) "TO_NUMBER(:TR116/10000)"
,TR119 POSITION(120:124) "TO_NUMBER(:TR119/1000)"
,TR120 POSITION(125:131)
,TR121 POSITION(132:132)
,TR122 POSITION(133:133)
,TR123 POSITION(134:145) "TO_NUMBER(:TR123/10000)"
,TR124 POSITION(146:160)
,TR127 POSITION(161:161)
,TR128 POSITION(162:169)
,TR129 POSITION(170:177)
,TR130 POSITION(178:185)
,TR131 POSITION(186:192)
,TR132 POSITION(193:198)
,TR133 POSITION(199:205)
,TR134 POSITION(206:215) "TO_NUMBER(:TR134/10000)"
,TR135 POSITION(216:222)
,TR136 POSITION(223:229)
,TR138 POSITION(230:242) "TO_NUMBER(:TR138/10000)"
,TR139 POSITION(243:257)
,TR140 POSITION(258:271) "TO_NUMBER(:TR140/100)"
,TR141 POSITION(272:280) "TO_NUMBER(:TR141/1000000)"
,TR144 POSITION(281:286)
,TR145 POSITION(287:300) "TO_NUMBER(:TR145/100)"
,TR146 POSITION(301:309) "TO_NUMBER(:TR146/10000)"
,TR147 POSITION(310:310)
,TR197 POSITION(311:320)
,TR199 POSITION(321:360)
,TR196 POSITION(361:370)
,TR148 POSITION(371:375) "TO_NUMBER(:TR148/1000)"
,TR149 POSITION(376:380)
,TR150 POSITION(381:383)
,TR151 POSITION(384:384)
,TR152 POSITION(385:386)
,TR153 POSITION(387:391)
,TR154 POSITION(392:392)
,TR155 POSITION(393:393)
,TR156 POSITION(394:404)
,TR157 POSITION(405:408)
,TR158 POSITION(409:417)
,TR159 POSITION(418:418)
,TR160 POSITION(419:419)
,TR161 POSITION(420:429)
,VERSION "0"
,CREATETIME sysdate
,CREATOR constant "TBPMS_TR1FIL_SG"
)