/**================================================================================================
@program: esoaf-fakement.js
@description: encode of platform custom methods.
@version: 1.0.20170419
=================================================================================================*/
var $faker={library:"! ? #",fixed:" "};function faker(str){if(!str)return;var restr="";var ary=$faker.library.split(" ");ary.push($faker.fixed);for(var i=0;i<str.length;i++){var index=Math.floor(Math.random()*ary.length);var rnum=Math.floor(Math.random()*10);for(var ii=0;ii<rnum;ii++){restr+=ary[index];}
restr+=str[i];};return escape(restr);}function unfaker(str){if(!str)return;return unescape(str).replace(/!|\s+|\?|#/gi,'');}
