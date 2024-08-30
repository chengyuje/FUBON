var FPS240ChartUtils = FPS240ChartUtils || function (mapping, canvasJsConfig) {
  var sortOrder = {
    deposit: {
      1: 2,
      3: 1,
      2: 3
    },
    fixed: {
      SN: 2,
      SI: 3,
      BND: 1
    },
    stock: {
      MFD: 1,
      ETF: 2,
      INS: 3
    },
    transaction: {
      MFD: 1,
      ETF: 2,
      INS: 3,
      BND: 4,
      SN: 5,
      SI: 6
    },
    fundName: {
      股票型: 1,
      債券型: 2,
      平衡型: 3,
      其他: 4
    }
  };

  var amountDataPoints = function (dataSource, showIndex) {
    return angular.copy(mapping.keys).map(function (item, index) {
      var i = '(' + (index + 1) + ') ';
      return {
        y: dataSource[item + 'Pct'],
        legendText: mapping.keysZhTw[index],
        indexLabel: (showIndex ? i : '') + dataSource[item + 'Pct'] + '%',
        indexLabelLineColor: 'transparent',
      };
    });
  };

  var stockDataPoints = function (dataSource, key, showIndex) {
    var temp = {};
    var cnt = 0;
    dataSource.forEach(function (row) {
      if (row.FUND_TYPE_NAME === '貨幣型') return;
      if (temp[row.FUND_TYPE_NAME || '平衡型'] === undefined) {
        temp[row.FUND_TYPE_NAME || '平衡型'] = {
          amt: row[key],
          legendText: row.FUND_TYPE_NAME || '平衡型',
          indexLabelLineColor: 'transparent'
        };
      } else {
        temp[row.FUND_TYPE_NAME || '平衡型'].amt += row[key];
      }
      cnt += row[key];
    });

    if (!cnt) {
      console.log('there is no cnt in stock dought');
      return [];
    }

    return Object.keys(temp)
      .sort(function (a, b) {
        return sortOrder.fundName[a] > sortOrder.fundName[b];
      }).map(function (key, index) {
        var i = '(' + (index + 1) + ') ';
        temp[key].y = parseInt(temp[key].amt / cnt * 100, 10);
        temp[key].indexLabel = (showIndex ? i : '') + temp[key].y + '%';
        return temp[key];
      });
  };

  // set chart table datas with colorsets and datapoints
  var setChartTable = function (chart, showIndex) {
    var chartColor = canvasJsConfig.bgcSets[chart.options.colorSet];
    var chartData = chart.options.data[0].dataPoints;
    var colorCnt = chartColor.length;

    return chartData.map(function (data, index) {
      var i = '(' + (index + 1) + ') ';
      return {
        color: chartColor[index % colorCnt],
        label: (showIndex ? i : '') + data.legendText,
        value: data.y,
        indexLabel: data.legendText
      };
    });
  };

  return {
    setChartTable: setChartTable,
    amountDataPoints: amountDataPoints,
    stockDataPoints: stockDataPoints
  };
};
