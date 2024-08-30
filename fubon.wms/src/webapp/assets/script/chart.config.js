var canvasJsConfig = {};
var blueSet = ['#9ee7ea', '#5acdd2', '#209ea4', '#118a95'];
var greenSet = ['#d2fba9', '#bceb3c', '#8fd711', '#67aa1a'];
var yellowSet = ['#ffee14', '#ffd451', '#ffbf56', '#fda411'];
var triSet = ['#ffd451', '#33a6ab', '#8fd711'];
var performanceSet = ['#0070c0', '#28b7ec', '#28b7ec', '#28b7ec', '#0070c0'];
var yearRatioSet = {
  raise: '#e96143',
  fall: '#8fd711'
};

canvasJsConfig.bgcSets = {
  blue: blueSet,
  green: greenSet,
  yellow: yellowSet,
  tri: triSet,
  performance: performanceSet,
};

canvasJsConfig.YRSet = yearRatioSet;

Object.keys(canvasJsConfig.bgcSets).forEach(function (item) {
  CanvasJS.addColorSet(item, canvasJsConfig.bgcSets[item]);
});
