/**
 * print type
 * **********
 * header
 * footer
 * image
 *
 */

/**
 * print cell
 * **********
 * cell(default)
 * wrapper
 * wrapper: start
 * wrapper-ignore => times
 * repeat(thead)
 *
 */

/**
 * print style
 * ***********
 * width: full | init(default)
 * page: next
 * top-margin: margin
 */

var html2Pdf = (function () {
  'use strict';

  /**
   * page format
   * **********
   * maxHeight
   * maxWidth
   * effectWidth
   * header
   * footer
   * margin
   *
   */

  // 595.28,  841.89
  const a4 = {
    maxHeight: 841.89,
    maxWidth: 595.28,
    effectWidth: 555.28,
    effectHeight: 821.89,
    header: 20,
    footer: 20,
    margin: 10,
    scale: 1.5
  };

  const PAGE_FORMAT = {
    a4: a4
  };

  const PRINT_TYPE = ['header', 'footer', 'image'];

  const RECT_SIZE_DIRECTION = {
    height: ['top', 'bottom'],
    width: ['left', 'right']
  };

  const RECT_SIZE_TYPE = {
    inner: ['padding'],
    default: ['padding', 'border'],
    outer: ['padding', 'border', 'margin'],
  };

  const DESTRUCT_NODE_ERROR_MSG = {
    overSize: 'element can\'t  be destructed, please read document and add attributes'
  };


  /**
   * calRealHeight
   * @param {number} w - width
   * @param {dom} el - el
   * @param {number} s - scale
   */
  const calHeight = (w, el) => (w * getRectSizeOf('height', 'default', el) / getRectSizeOf('width', 'default', el)) || 0;

  const getHiddenDom = function (enterPoint) {
    let hiddenDom = document.getElementById('hiddendom');
    const enterPointDom = !!enterPoint ? document.querySelector(enterPoint) : null;
    if (!hiddenDom) {
      hiddenDom = document.createElement('div');
      hiddenDom.setAttribute('id', 'hiddendom');
      hiddenDom.setAttribute('style', 'position:relative;left:-100vw;');
      if (!!enterPointDom)
        enterPointDom.appendChild(hiddenDom);
      else
        document.body.appendChild(hiddenDom);
    }
    return hiddenDom;
  };

  const clearChildDom = function (el) {
    while (el.firstChild) {
      el.firstChild.remove();
    }
  };

  /**
   * calRealHeightByElement
   * @param {object} pageFormat
   * @param {string} enterPoint
   * @returns () => calculate length of el
   */
  const calRealHeightByElement = (pageFormat, enterPoint) => {
    const hiddenDom = getHiddenDom(enterPoint);
    return (el) => {
      const _el = el.cloneNode(true);

      hiddenDom.appendChild(_el);
      const length = calHeight(pageFormat.effectWidth, _el);
      clearChildDom(hiddenDom);
      return length;
    };
  };

  const getNodeAttribute = (node, attr) => (node.getAttribute(attr) || '').replace(/\s/g, '').split(',');
  const nodeHasAttribute = (node, attr, key) => getNodeAttribute(node, attr).indexOf(key) >= 0;

  /**
   * node to canvas in object
   * @param {dom} node
   * @param {string} enterPoint -enterpoint
   */
  const node2png = function node2png(node, enterPoint) {
    return new Promise(function (resolve) {
      // default scale 1.5
      const printType = node.getAttribute('print');

      const calRealHeight = calRealHeightByElement(a4, enterPoint);
      console.log('--- --- start --- ---');
      console.info('node-height: ' + calRealHeight(node));

      if (!document.body.contains(node))
        getHiddenDom(enterPoint).appendChild(node);
      if (printType === 'image') {
        clearChildDom(getHiddenDom(enterPoint));
        return resolve({
          node: node,
          canvas: null,
          dataURL: node.getAttribute('src'),
          width: node.width,
          height: node.height,
          printType: node.getAttribute('print'), // default | header | image
          options: {
            pageAction: node.getAttribute('print-page'),
            widthAction: node.getAttribute('print-width'), // default | full
            topMargin: node.getAttribute('print-top-margin'), // 10px
            scaleDirection: node.getAttribute('print-scale-direction') // default: horizontal | vertical
          }
        });
      } else {
        html2canvas(node, {
            imageTimeout: 1000,
            scale: 2.5
          })
          .then(canvas => {
            console.info('canvas-height: ' + (a4.effectWidth * canvas.height / canvas.width));
            console.log('--- --- end --- ---');
            clearChildDom(getHiddenDom(enterPoint));
            return resolve({
              node: node,
              canvas: canvas,
              dataURL: canvas.toDataURL('image/png'),
              width: canvas.width,
              height: canvas.height,
              printType: printType,
              options: {
                pageAction: node.getAttribute('print-page'),
                widthAction: node.getAttribute('print-width'), // default | full
                topMargin: node.getAttribute('print-top-margin'), // 10px
                scaleDirection: node.getAttribute('print-scale-direction') // default: horizontal | vertical
              }
            });
          });
      }
    });
  };

  /**
   * getCanvas (recursive)
   * @param {[dom]} elList
   * @param {int} cnt
   * @param {function} check - check done callback
   * @param {function} done - done callback
   * @param {array} arr - final list
   * @returns arr
   */
  const getCanvas = function (elList, cnt, check, done, arr) {
    if (!arr) arr = [];
    node2png(elList[cnt++])
      .then(function (obj) {
        arr.push(obj);
        if (check(cnt)) getCanvas(elList, cnt, check, done, arr);
        else if (!!done) done(arr);
      });
  };

  /**
   * generateCanvas
   * @param {[string]} selectorList
   * @param {string} type
   *
   * todo
   * @param {obejct} callbacks
   * @param {function} callbacks.header(pdfObj, page) => pdfObj
   * @param {function} callbacks.footer(pdfObj, page) => pdfObj
   *
   * @param {object} options
   * @param {string} options.enterPoint - default document.body
   * @param {string} options.pageFormat - a4 as default
   * @param {string} options.pdfName - type = save
   * @param {string} options.footer - footer
   * @param {string} options.footer.leftText - left
   * @param {string} options.footer.rightText - right
   * @param {string} options.footer.pageText - page
   */
  const generateCanvas = function generateCanvas(selectorList, type, callbacks, options) {
    /* init */
    options.footer = options.footer || {};

    // page view: p = vertical, l = horizontal
    // unit: mm pt cm ...
    // page size: default a4
    const pageFormat = options.pageFormat || 'a4';
    const pageFormatOption = PAGE_FORMAT[options.pageFormat || 'a4'];
    const calRealHeight = calRealHeightByElement(pageFormatOption, options.enterPoint);
    const _this = {
      pageFormat,
      pageFormatOption,
      calRealHeight
    };

    return new Promise((resolve) => {
      let nodeList = [];
      let elList = [];

      selectorList = Array.isArray(selectorList) ? selectorList : [selectorList];
      // get Nodes
      selectorList.forEach(function (selector) {
        let els = document.querySelectorAll(selector);
        els.forEach(function (el) {
          el.style.backgroundColor = '#fff';
          nodeList.push(el);
        });
      });

      // check height start: make virtual dom to count height if over size for page
      const headerCanvasObjs = nodeList.filter((el) => nodeHasAttribute(el, 'print', 'header'));
      const headerHeight = headerCanvasObjs.length > 0 ? calRealHeight(headerCanvasObjs[0]) : pageFormatOption.header;
      const footerCanvasObjs = nodeList.filter((el) => nodeHasAttribute(el, 'print', 'footer'));
      const footerHeight = footerCanvasObjs.length > 0 ? calRealHeight(footerCanvasObjs[0]) : pageFormatOption.footer;
      // maxHeight for precalculate pages
      // if node, its 1.5 bigger (?)
      const maxHeight = pageFormatOption.maxHeight - headerHeight - pageFormatOption.margin - footerHeight;

      let cntHeight = 0;
      nodeList.forEach(function (el) {
        // debugger;
        if (nodeHasAttribute(el, 'print', 'header') || nodeHasAttribute(el, 'print', 'footer')) return elList.push(el);
        const h = Math.ceil(calRealHeight(el));
        const page = el.getAttribute('print-page');
        const uniq = el.getAttribute('print-unique');
        const topMargin = Number(el.getAttribute('print-top-margin')) || 0;

        if (h > maxHeight || (h + cntHeight) > maxHeight) {
          // todo: uniq find out destructNode cloneNode logic
          if (uniq === 'true') {
            cntHeight = h + topMargin;
            elList.push(el);
          } else {
            const {
              decomposedElList,
              leftHeight,
            } = destructNode.call(_this, el, cntHeight, maxHeight);
            // margin
            cntHeight = leftHeight + topMargin;
            elList = elList.concat(decomposedElList);
          }
        } else {
          // margin
          cntHeight += h + topMargin;
          elList.push(el);
        }

        if (page === 'next')
          cntHeight = 0;

        return true;
      });

      // check height end
      // debugger;
      console.info(elList);

      // get Canvas
      const size = elList.length;

      // recursive output [canvas]
      return getCanvas(elList, 0,
        (num) => num < size,
        (arr) => resolve(generatePDF.call(_this, arr, type, callbacks, options))
      );
    });
  };


  /**
   * destructNode
   * @param {dom} el - attr = 'print'
   * @param {number} currentHeight
   * @param {number} limitHeight
   * @returns {object} decomposedElList, leftHeight, e
   */
  const destructNode = function destructNode(el, currentHeight, limitHeight) {
    const that = this;
    const rtnObj = {
      decomposedElList: [],
      leftHeight: currentHeight,
      e: [],
    };

    let printIsWrapper = true;

    // print root with no wrapper
    let printRoot = el.cloneNode(true);
    let printRootFindWrapper = el.querySelectorAll('[print-wrapper]');
    if (!printRootFindWrapper || printRootFindWrapper.length <= 0) {
      // no wrapper
      printRoot.setAttribute('print-root-target', undefined);
      while (printRoot.firstChild) {
        printRoot.firstChild.remove();
      }
    } else {
      printIsWrapper = false;
      // clear wrapper
      let tmp = printRoot.querySelectorAll('[print-wrapper]');
      tmp[0].parentNode.setAttribute('print-root-target', undefined);
      tmp.forEach(dom => dom.remove());
    }

    // print wrapper
    let wrappers = queryCloneNode(el, '[print-wrapper]', true);
    if (!!printIsWrapper) wrappers = [el.cloneNode(true)];

    let cntHeight = currentHeight;
    let cntTmpHeight = currentHeight;
    let cntRoot = printRoot.cloneNode(true);
    let cntAppends = 0;

    // sugar function: reset cnt values
    const nextWrapPage = (ws, h) => {
      cntAppends = 0;
      cntRoot = printRoot.cloneNode(true);
      (cntRoot.querySelector('[print-root-target]') || cntRoot)
      .appendChild(ws.cloneNode(true));
      const ignoreList = cntRoot.querySelectorAll('[print-once-ignore]');
      if (!!ignoreList && ignoreList.length > 0) {
        ignoreList.forEach((ignore) => ignore.remove());
      }
      cntHeight = (h || 0) + this.calRealHeight(cntRoot);
      cntTmpHeight = cntHeight;
      return cntRoot;
    };

    wrappers.forEach(function (ws) {
      // debugger;

      // --------------------------------------------------
      // try to push as mush ws
      let tmpNode = cntRoot.cloneNode(true);
      (tmpNode.querySelector('[print-root-target]') || tmpNode)
      .appendChild(ws.cloneNode(true));
      const cntPrWsHeight = that.calRealHeight(tmpNode);
      // push wrapper 1 by 1
      // if does fit, append and go next wrapper
      if (cntPrWsHeight + cntTmpHeight <= limitHeight) {
        (cntRoot.querySelector('[print-root-target]') || cntRoot)
        .appendChild(ws);
        cntAppends += 1;
        return cntHeight += cntPrWsHeight;
      }

      const currentPrWs = printRoot.cloneNode(true);
      (currentPrWs.querySelector('[print-root-target]') || currentPrWs)
      .appendChild(ws.cloneNode(true));
      const currentPrWsHeight = that.calRealHeight(currentPrWs);
      const currentPrWsCells = ws.querySelectorAll('[print-cell]');

      // --------------------------------------------------
      // if ws has no cells

      if (!currentPrWsCells || currentPrWsCells.length <= 0) {
        // skip this wrapper
        if (currentPrWsHeight > limitHeight) {
          // no attribute to decompose
          console.error(DESTRUCT_NODE_ERROR_MSG.overSize);
          rtnObj.e.push(DESTRUCT_NODE_ERROR_MSG.overSize);
        }
        // put next page and create new page
        else {
          if (cntAppends > 0) {
            rtnObj.decomposedElList.push(cntRoot.cloneNode(true));
          }
          nextWrapPage(ws);
        }
        return false;
      }

      // --------------------------------------------------
      // wrapper can't fit, try decompose cells

      // wrapper with no cell
      let printWs = ws.cloneNode(true);
      printWs.querySelector('[print-cell]').parentNode.setAttribute('print-wrapper-target', undefined);
      printWs.querySelectorAll('[print-cell]').forEach(dom => dom.remove());

      tmpNode = cntRoot.cloneNode(true);
      (tmpNode.querySelector('[print-root-target]') || tmpNode)
      .appendChild(printWs.cloneNode(true));

      // push cell 1 by 1 wrapper
      currentPrWsCells.forEach(function (cell) {
        // debugger;

        tmpNode.querySelector('[print-wrapper-target]')
          .appendChild(cell.cloneNode(true));
        const tmpPrWsPcHeight = that.calRealHeight(tmpNode);
        // not exceed
        if (tmpPrWsPcHeight + cntTmpHeight <= limitHeight) {
          // cntRoot not yet has printWs
          if (!cntRoot.querySelector('[print-wrapper-target]'))(cntRoot.querySelector('[print-root-target]') || cntRoot).appendChild(printWs.cloneNode(true));
          cntRoot.querySelector('[print-wrapper-target]').appendChild(cell.cloneNode(true));
          cntAppends += 1;
          return tmpPrWsPcHeight;
        }
        // exceed
        else {
          if (cntAppends > 0) {
            rtnObj.decomposedElList.push(cntRoot.cloneNode(true));
          }
          nextWrapPage(printWs);
          cntRoot.querySelector('[print-wrapper-target]').appendChild(cell.cloneNode(true));
          tmpNode = cntRoot.cloneNode(true);
          cntAppends += 1;
        }
      });
      cntTmpHeight = cntHeight;
    });


    // last el
    rtnObj.decomposedElList.push(cntRoot.cloneNode(true));
    rtnObj.leftHeight = this.calRealHeight(cntRoot);

    return rtnObj;
  };

  /**
   * generatePDF
   * @param {object} canvasObjs
   * @param {string} type
   *
   * @param {obejct} callbacks
   * @param {function} callbacks.header(pdfObj, page, pageFormatOption) => pdfObj
   * @param {function} callbacks.footer(pdfObj, page, pageFormatOption) => pdfObj
   *
   * @param {object} options
   * @param {string} options.pageFormat - a4 as default
   * @param {string} options.pdfName - type = save
   * @param {string} options.footer - footer
   * @param {string} options.footer.leftText - left
   * @param {string} options.footer.rightText - right
   * @param {string} options.footer.pageText - page
   */
  const generatePDF = function generatePDF(canvasObjs, type, callbacks, options) {
    /**
     * constructor:
     * @param pageFormat
     * @param pageFormatOption
     * @param calRealHeight
     */
    const that = this;
    const cntHorizontal = (obj) => {
      return {
        w: obj.options.widthAction === 'full' ? that.pageFormatOption.maxWidth : that.pageFormatOption.effectWidth,
        h: (obj.options.widthAction === 'full' ? that.pageFormatOption.maxWidth : that.pageFormatOption.effectWidth) * obj.height / obj.width
      };
    };

    const headerCanvasObjs = canvasObjs.filter((el) => nodeHasAttribute(el.node, 'print', 'header'));
    const headerSize = headerCanvasObjs.length > 0 ? cntHorizontal(headerCanvasObjs[0]) : null;
    const headerHeight = (!!headerSize ? headerSize.h : this.pageFormatOption.header) || 0;

    const footerCanvasObjs = canvasObjs.filter((el) => nodeHasAttribute(el.node, 'print', 'footer'));
    const footerSize = footerCanvasObjs.length > 0 ? cntHorizontal(footerCanvasObjs[0]) : null;
    const footerHeight = (!!footerSize ? footerSize.h : this.pageFormatOption.footer) || 0;

    let maxHeight = this.pageFormatOption.maxHeight - footerHeight;

    // pdf Start from here
    let pdfObj = new jsPDF('p', 'pt', this.pageFormat, true);
    let heightCnt = 0;
    pdfObj.page = 1;

    // sugar function: cnt size
    const cntSize = function (obj) {
      if (!obj.options.scaleDirection) {
        return cntHorizontal(obj);
      } else {
        return {
          h: that.pageFormatOption.maxHeight - heightCnt - footerHeight,
          w: (that.pageFormatOption.maxHeight - heightCnt - footerHeight) * obj.width / obj.height
        };
      }
    };

    const setheader = (banner, w, h, isFull) => () => pdfObj.addImage(banner, 'png', isFull ? 0 : 20, 0, w, h, null, 'SLOW');

    const header = !!headerSize ?
      setheader(headerCanvasObjs[0].dataURL, headerSize.w, headerSize.h, headerCanvasObjs[0].options.widthAction === 'full') :
      () => true;

    const footer = () => {
      pdfObj.setFontSize(8);
      if (!!options.footer.leftText)
        pdfObj.text(options.footer.leftText, 20, that.pageFormatOption.maxHeight - 10);

      pdfObj.text((options.footer.page || '') + pdfObj.page.toString(), that.pageFormatOption.maxWidth / 2 - 10, that.pageFormatOption.maxHeight - 10);

      if (!!options.footer.right)
        pdfObj.text(pdfObj.footer.rightText, that.pageFormatOption.maxWidth - 20, that.pageFormatOption.maxHeight - 10);
      pdfObj.page++;
      return pdfObj.page;
    };

    // sugar function: new page
    const newPage = function () {
      pdfObj.addPage();
      header();
      footer();
      heightCnt = headerHeight + that.pageFormatOption.margin;
    };

    // first page
    header();
    footer();
    heightCnt = headerHeight + that.pageFormatOption.margin;
    // canvas => pdf
    canvasObjs.forEach(function (obj, index) {
      // debugger;

      // ignore header footer
      if (nodeHasAttribute(obj.node, 'print', 'header') || nodeHasAttribute(obj.node, 'print', 'footer')) return false;

      const {
        w,
        h
      } = cntSize(obj);

      if (!w || !h || !obj.width || !obj.height) {
    	debugger;
        console.error('there has no img');
        return false;
      }

      // the block > maxHeight
      if (h > maxHeight) {
        console.error('there is a page over size');
        return false;
      }

      // the block + cntHeight > maxHeight
      if (heightCnt + h > maxHeight) {
        newPage();
      }

      if (obj.options.widthAction === 'full') {
        pdfObj.addImage(obj.dataURL, 'png', 0, (heightCnt) + (Number(obj.options.topMargin) || 0), w, h, null, 'SLOW');
      } else {
        pdfObj.addImage(obj.dataURL, 'png', 20, (heightCnt) + (Number(obj.options.topMargin) || 0), w, h, null, 'SLOW');
      }
      // margin
      heightCnt += h + Number(obj.options.topMargin);

      // force next page
      if (obj.options.pageAction === 'next') {
        newPage();
      }

      return index;
    });

    // debugger;
    if (type !== 'save') {
      return pdfObj.output(type);
    } else {
      pdfObj.save(options.pdfName || (getFormattedDate() + '.pdf'));
    }
  };

  /**
   * queryCloneNode - query includes self
   * @param {dom} node
   */
  function queryCloneNode(node, key, isAll) {
    if (!node) return false;
    if (!!isAll) {
      const _node = node.cloneNode(true).querySelectorAll(key);
      return !!_node && _node.length > 0 ? _node : [];
    } else {
      const _node = node.cloneNode(true).querySelector(key);
      return !!_node ? _node : node.cloneNode(true);
    }
  }

  /**
   * getCntHeight - calculate height, width
   * @param {string} target - height, width
   * @param {string} type - default(border), inner(padding), outer(margin)
   * @param {dom} el
   */
  function getRectSizeOf(target, type, el) {
    const style = window.getComputedStyle(el);
    const length = [target].concat((RECT_SIZE_TYPE[type || 'default'] || [])
        .reduce((current, style) => current.concat((RECT_SIZE_DIRECTION[target] || [])
          .map(direction => style + '-' + direction + (style === 'border' ? '-width' : ''))), [])
      )
      .map((key) => Number(style.getPropertyValue(key).replace(/[A-Za-z]/g, '')))
      .reduce((a, b) => a + b, 0);

    return length;
  }

  /**
   * getFormattedDate - get formatted date time
   * @param {date} date
   * @param {string} format
   */
  function getFormattedDate(date, format) {
    date = date || new Date();
    format = format || 'yyyyMMdd';

    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    const hour = ('0' + date.getHours()).slice(-2);
    const minute = ('0' + date.getMinutes()).slice(-2);

    return format
      .replace('yyyy', year)
      .replace('MM', month)
      .replace('dd', day)
      .replace('HH', hour)
      .replace('mm', minute);
  }


  // export modules
  return {
    getPdf: generateCanvas
  };

})();
