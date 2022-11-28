  (function (factory) {
  /* global define */
  if (typeof define === 'function' && define.amd) {
    // AMD. Register as an anonymous module.
    define(['jquery'], factory);
  } else {
    // Browser globals: jQuery
    factory(window.jQuery);
  }
}(function ($) {
  // template

  var KEY = {
    UP: 38,
    DOWN: 40,
    ENTER: 13
  };

  var DROPDOWN_KEYCODES = [KEY.UP, KEY.DOWN, KEY.ENTER];
 
  var tmpl = $.summernote.renderer.getTemplate();

  // core functions: range, dom
  var range = $.summernote.core.range;
  var dom = $.summernote.core.dom;

  var eventHandler = $.summernote.eventHandler;
  var renderer = $.summernote.renderer;
  
  var prop = {};
  var $popover = $('<div />').addClass('hint-group').css({
        'position': 'absolute',
        'max-height': 150,
        'z-index' : 999,
        'overflow' : 'hidden',
        'display' : 'none',
        'border' : '1px solid gray',
        'border-radius' : '5px'
  });

    $.summernote.addPlugin({
    
    /** @property {String} name name of plugin */
    name: 'cifra2',

    /**
     * create list item template
     *
     * @interface
     * @param {Object} search
     * @returns {Array}  created item list
     */
    template : function(item) {
        return "[<strong>" + item.key + "</strong>] " + item.titolo;
    },

    /**
     * create inserted content to add  in summernote
     *
     * @interface
     * @param {String} html
     * @param {String} keyword
     * @return {HTMLEleemnt|String}
     */
    content : function(item) {
      return "" + item.key + "" ;
    },

    /**
     * load search list
     *
     * @interface
     */
    load : function($popover) {
        $popover.css({ 'max-width' : '500px'  })
    },

    /**
     * @param {jQuery} $node
     */
    scrollTo: function ($node) {
      var $parent = $node.parent();
      $parent[0].scrollTop = $node[0].offsetTop - ($parent.innerHeight() / 2);
    },

    /**
     * @param {jQuery} $popover
     */
    moveDown: function ($popover) {
      var index = $popover.find('.active').index();
      this.activate($popover, (index === -1) ? 0 : (index + 1) % $popover.children().length);
    },

    /**
     * @param {jQuery} $popover
     */
    moveUp: function ($popover) {
      var index = $popover.find('.active').index();
      this.activate($popover, (index === -1) ? 0 : (index - 1) % $popover.children().length);
    },

    /**
     * @param {jQuery} $popover
     * @param {Number} i
     */
    activate: function ($popover, idx) {
      idx = idx || 0;

      if (idx < 0) {
        idx = $popover.children().length - 1;
      }

      $popover.children().removeClass('active');
      var $activeItem = $popover.children().eq(idx);
      $activeItem.addClass('active');

      this.scrollTo($activeItem);
    },

    /**
     * @param {jQuery} $popover
     */
    replace: function ($popover, editor, layoutInfo) {
      
      var $activeItem = $popover.find('.active');
      var content = this.content($activeItem.data('item'));

       
        var $editable = layoutInfo.editable();
      
        var p = prop.info[content];
        editor.beforeCommand($editable);
        /*self.iteraCrea(value, p);*/
        if('color' ===  p.template ){
         // editor.backColor($editable, '[[${' + p.key + '}]]');
        }else if('text' ===  p.template ){
          var img = $('<span>[[${' + p.key + '}]]</span>');
          editor.insertNode($editable, img[0]);
        }else if('html' ===  p.template ){
          var img = $('<div th:utext="${'+p.key+'}">+${' + value + '}</div>');
          editor.insertNode($editable, img[0]);
        }else if('image64' ===  p.template ){
          var img = $('<img th:attr="src=${'+p.key+'}" width="80" height="150"></img>');
          editor.insertNode($editable, img[0]);
        }else if('table' ===  p.template ){
          console.log("p.fields"+p.fields);
          if(p.fields  === 'undefined')
            return;

          var table_start = '<table width="100%" class="table table-bordered"><tbody>';
          var table_end = '</tbody></table>';

          table_start += '<tr>';
          var td_titolo ='';
          for(var i=0; i< p.fields.length; i++){
              var field = p.fields[i];
              td_titolo += '<td><span>'+ field.titolo +'</span></td>';
          }
          table_start += td_titolo;
          table_start += '</tr>';
          table_start += '<tr th:each ="item: ${'+ p.key+'}">';

          for(var i=0; i< p.fields.length; i++){
              var field = p.fields[i]; 
              var xkey = field.key;
             
              xkey = 'item.' + xkey;
              if('color' ===  p.template ){
               // editor.backColor($editable, '[[${' + xkey + '}]]');
              }else if('text' ===  field.template ){
                table_start += '<td>[[${' + xkey + '}]]</td>';
              }else if('html' ===  field.template ){
                table_start +='<td><div th:utext="${'+xkey+'}">'+field.key+'</div></td>';
              }else if('image64' ===  field.template ){
                table_start +='<td><img th:attr="src=${'+xkey+'}" width="80" height="150"></img></td>';
              }
          }

          table_start += '</tr>';
          table_start += table_end;
          var img = $(table_start);
          editor.insertNode($editable, img[0]);
        }else if('list' ===  p.template ){
          console.log("p.fields"+p.fields);
          if(p.fields  === 'undefined')
            return;

          var table_start = '<table width="100%" ><tbody>';
          var table_end = '</tbody></table>';

          /*table_start += '<tr>';
          var td_titolo ='';
          for(var i=0; i< p.fields.length; i++){
              var field = p.fields[i];
              td_titolo += '<td><span>'+ field.titolo +'</span></td>';
          }
          table_start += td_titolo;
          table_start += '</tr>';*/
          table_start += '<tr th:each ="item: ${'+ p.key+'}">';
          table_start += '<td>';
          for(var i=0; i< p.fields.length; i++){
              var field = p.fields[i]; 
              var xkey = field.key;
              
              xkey = 'item.' + xkey;
              if('color' ===  p.template ){
               // editor.backColor($editable, '[[${' + xkey + '}]]');
              }else if('text' ===  field.template ){
                table_start += '<p><span>[[${' + xkey + '}]]</span></p>';
              }else if('html' ===  field.template ){
                table_start +='<p><div th:utext="${'+xkey+'}">'+field.key+'</div></p>';
              }else if('image64' ===  field.template ){
                table_start +='<p><img th:attr="src=${'+xkey+'}" width="80" height="150"></img></p>';
              }
          }
          table_start += '</td>';
          table_start += '</tr>';
          table_start += table_end;
          var img = $(table_start);
          editor.insertNode($editable, img[0]);
        }
        editor.afterCommand($editable); 
    },

    /**
     * @param {String} keyword
     * @return {Object|null}
     */
    searchFields: function (callback) {
        if(this.ricerca === null){
          callback();
        }else{
          this.ricerca(callback);
        }
    },


    createTemplate: function (prop) {
      var items  = [];
      var list =  [];
      for (var i = 0, len = prop.keys.length; i < len; i++) {
        var key = prop.keys[i];
        var p = prop.info[key];
        if('divider' ===  p.template){
            var $item = $('<a class="divider list-group-item"></a>');
        }else{
            //var $item = $('<a class="list-group-item" data-event="proprieta" data-value="'+ key +'"></a>');
            var $item = $('<a class="list-group-item" data-value="'+ key +'"></a>');
            $item.append(this.template(p));
            $item.data('item', p);
            items.push($item);
        }
        
      }

      if (items.length) {
        items[0].addClass('active');
      }

      return items;
    },

    ricerca : null,

    /*ricerca: function (callback) {
      callback();
    },*/

    throttle : 30,

    init: function (layoutInfo) {
     
      var self = this;
      var $editor = layoutInfo.editor()
      var $editable = layoutInfo.editable();

      var $toolbar = layoutInfo.toolbar();
      var $btn = $toolbar.find('button[data-event="omissis"]');

      $editable.on('click', function (event) {
        var okclass = $(event.target).attr("omissis");
        if(okclass){
          //console.log("omissis");
          $btn.toggleClass('active', true);
        }else{
          $btn.toggleClass('active', false);
        }
      });

      $editor.on('keyup', function (event) {   
        var rng = range.create();
        if(rng===null)
          return;

        var trovato = rng ? rng.isOnEditable() : false; 
        var $cont = $(dom.isText(rng.sc) ? rng.sc.parentNode : rng.sc);
        var okclass = $cont.attr("omissis");
        if(okclass && trovato){
          //console.log("omissis");
          $btn.toggleClass('active', true);
        }else{
          $btn.toggleClass('active', false);
        }
        //event.preventDefault();
      });


      var $note = layoutInfo.holder();
      

      /*$popover.on('click', '.list-group-item', function HintItemClick(nativeEvent) {
        //console.log(nativeEvent);
        nativeEvent.preventDefault();
        self.replace($popover,$.summernote.eventHandler.getModule(),layoutInfo);

        $popover.hide();
        $note.summernote('focus');
      });*/


      $(document).on('click', function HintClick() {
        $popover.hide();
      });

      $note.on('summernote.keydown', function HintKeyDown(customEvent, nativeEvent) {
        if ($popover == null) {
          return true;
        }

        if ($popover.css('display') !== 'block') {
          return true;
        }

        if (nativeEvent.keyCode === KEY.DOWN) {
          nativeEvent.preventDefault();
          self.moveDown($popover);
        } else if (nativeEvent.keyCode === KEY.UP) {
          nativeEvent.preventDefault();
          self.moveUp($popover);
        } else if (nativeEvent.keyCode === KEY.ENTER) {
          nativeEvent.preventDefault();
          // nativeEvent.stopPropagation();
          self.replace($popover,$.summernote.eventHandler.getModule(),layoutInfo);
          $popover.hide();
          $note.summernote('focus');   
        }
       
      });

      $note.on('summernote.keyup', function HintKeyUp(customEvent, nativeEvent) {
        if (DROPDOWN_KEYCODES.indexOf(nativeEvent.keyCode) > -1) {
          if (nativeEvent.keyCode === KEY.ENTER) {
            if ($popover.css('display') === 'block') {
              return false;
            }
          }
        }
      });

      var $btnFields = $toolbar.find('button[data-name="proprieta"]');
      $btnFields.on('click', function (event){
          event.preventDefault();
          $popover.hide();
          var timer = null;
          clearTimeout(timer);
          timer = setTimeout(function () {
            self.searchFields(function(uri){
              var dropdownTmp ;  
              //var uriFile = 'scripts/components/summernote/plugin/proprieta.json';
              console.log(uri);
              if(!uri){
                $popover.hide();
                return;
              }

              if(uri ==='undefined') {
                $popover.hide();
                return;
              }

              $.getJSON(uri).then(function (data) {
                //console.log(data);
                prop.keys = Object.keys(data);
                prop.info = data;

                //console.log(dropdownTmp);

                layoutInfo.popover().append($popover);

                 var rect = {'left':100,'top':100};
                  console.log(rect);

                $popover.html(self.createTemplate(prop)).css({
                  left: rect.left,
                  top: rect.top 
                }).show();
                //$popover.focus();
                $note.summernote('focus');   
              });
            }); 
          }, self.throttle); //timer
    });//btn

      this.load($popover);
    },

    buttons: { // buttons

      omissis: function () {

        return tmpl.iconButton('fa fa-user-secret', {
          event : 'omissis',
          title: 'omissis'
        });
      },

      pagebreak: function () {

        return tmpl.iconButton('fa fa-file-o', {
          event : 'pagebreak',
          title: 'nuova pagina',
          hide: true
        });
      },

      proprieta: function () {

        return tmpl.iconButton('fa fa-header', {
          title: 'Aggiungi Campo Documento',
          hide: true
        });
      }
    },

    events: { // events

      omissis: function (event, editor, layoutInfo) {
       
        // Get current editable node
        var $editable = layoutInfo.editable();

        /*console.log('$editable:'+$editable);
        console.log($editable);*/
        

        var rng = range.create();
       
        if(rng===null)
          return;

        var nodes = rng.nodes(null, {includeAncestor:true ,fullyContains: true });
        //console.log(nodes);
        if (nodes !== 'undefined' && nodes.length!==0) {
           
          //console.log('nodes select:'+nodes);
          for(var i =0; i < nodes.length ; i++){
                var node = nodes[i];
                if (dom.isText(node)) {
                  var anc = node.parentNode;
                  //console.log(anc); 
                  //console.log(node); 
                  //console.log('node :'+i+':' +node);      
                  var okclass = $(anc).attr("omissis");
                  if(okclass){
                    editor.beforeCommand($editable);
                    //dom.appendChildNodes(anc.parentNode,anc.childNodes);
                    dom.remove(anc,false);
                    //editor.removeFormat(node);
                    editor.afterCommand($editable);  
                  }else{
                    editor.beforeCommand($editable);
                    dom.wrap(node,"omissis title='omissis' omissis='omissis' style='background-color: rgb(255, 214, 99);'");
                    editor.afterCommand($editable);  
                  }
                }      
          }
        }else if (dom.isText(rng.sc)) {
          //console.log(rng.sc.data);
          var strdata = rng.sc.data;
          var str = strdata.substring(rng.so,rng.eo);
          //console.log(str);
          
          // deleteContents on range.
          //rng = rng.deleteContents();       
          rng = rng.splitText();
          // finding 
          var splitRoot = dom.ancestor(rng.sc, dom.isText);
          //console.log(splitRoot);
          var nextPara;
          if (splitRoot) {
            nextPara = dom.splitTree(splitRoot, rng.getStartPoint());
            //console.log(nextPara);
          } 
          var node = rng.sc;
          var anc = node.parentNode;
          //console.log(anc);
          var okclass = $(anc).attr("omissis");
          if(okclass){
            editor.beforeCommand($editable);
            //dom.appendChildNodes(anc.parentNode,anc.childNodes);
            dom.remove(anc,false);
            //editor.removeFormat(node);
            editor.afterCommand($editable);  
          }else{
            editor.beforeCommand($editable);
            dom.wrap(node,"omissis title='omissis' omissis='omissis' style='background-color: rgb(255, 214, 99);'");
            editor.afterCommand($editable);  
          }

          
        }

      },

      proprieta: function (event, editor, layoutInfo, value) {
        //var self = this;
        // Get current editable node
        var $editable = layoutInfo.editable();
      
        var p = prop.info[value];
        editor.beforeCommand($editable);
        /*self.iteraCrea(value, p);*/
        if('color' ===  p.template ){
         // editor.backColor($editable, '[[${' + p.key + '}]]');
        }else if('text' ===  p.template ){
          var img = $('<span>[[${' + p.key + '}]]</span>');
          editor.insertNode($editable, img[0]);
        }else if('html' ===  p.template ){
          var img = $('<div th:utext="${'+p.key+'}">+${' + value + '}</div>');
          editor.insertNode($editable, img[0]);
        }else if('image64' ===  p.template ){
          var img = $('<img th:attr="src=${'+p.key+'}" width="80" height="150"></img>');
          editor.insertNode($editable, img[0]);
        }else if('table' ===  p.template ){
          console.log("p.fields"+p.fields);
          if(p.fields  === 'undefined')
            return;

          var table_start = '<table width="100%" class="table table-bordered"><tbody>';
          var table_end = '</tbody></table>';

          table_start += '<tr>';
          var td_titolo ='';
          for(var i=0; i< p.fields.length; i++){
              var field = p.fields[i];
              td_titolo += '<td><span>'+ field.titolo +'</span></td>';
          }
          table_start += td_titolo;
          table_start += '</tr>';
          table_start += '<tr th:each ="item: ${'+ p.key+'}">';

          for(var i=0; i< p.fields.length; i++){
              var field = p.fields[i]; 
              var xkey = field.key;
             
              xkey = 'item.' + xkey;
              if('color' ===  p.template ){
               // editor.backColor($editable, '[[${' + xkey + '}]]');
              }else if('text' ===  field.template ){
                table_start += '<td>[[${' + xkey + '}]]</td>';
              }else if('html' ===  field.template ){
                table_start +='<td><div th:utext="${'+xkey+'}">'+field.key+'</div></td>';
              }else if('image64' ===  field.template ){
                table_start +='<td><img th:attr="src=${'+xkey+'}" width="80" height="150"></img></td>';
              }
          }

          table_start += '</tr>';
          table_start += table_end;
          var img = $(table_start);
          editor.insertNode($editable, img[0]);
        }else if('list' ===  p.template ){
          console.log("p.fields"+p.fields);
          if(p.fields  === 'undefined')
            return;

          var table_start = '<table width="100%" ><tbody>';
          var table_end = '</tbody></table>';

          /*table_start += '<tr>';
          var td_titolo ='';
          for(var i=0; i< p.fields.length; i++){
              var field = p.fields[i];
              td_titolo += '<td><span>'+ field.titolo +'</span></td>';
          }
          table_start += td_titolo;
          table_start += '</tr>';*/
          table_start += '<tr th:each ="item: ${'+ p.key+'}">';
          table_start += '<td>';
          for(var i=0; i< p.fields.length; i++){
              var field = p.fields[i]; 
              var xkey = field.key;
              
              xkey = 'item.' + xkey;
              if('color' ===  p.template ){
               // editor.backColor($editable, '[[${' + xkey + '}]]');
              }else if('text' ===  field.template ){
                table_start += '<p><span>[[${' + xkey + '}]]</span></p>';
              }else if('html' ===  field.template ){
                table_start +='<p><div th:utext="${'+xkey+'}">'+field.key+'</div></p>';
              }else if('image64' ===  field.template ){
                table_start +='<p><img th:attr="src=${'+xkey+'}" width="80" height="150"></img></p>';
              }
          }
          table_start += '</td>';
          table_start += '</tr>';
          table_start += table_end;
          var img = $(table_start);
          editor.insertNode($editable, img[0]);
        }
        editor.afterCommand($editable); 
      },

      pagebreak : function (event, editor, layoutInfo) {
        var $editable = layoutInfo.editable();

        var img = $('<p style="page-break-before:always; border-bottom:1px dashed #000;" ></p>');
        editor.insertNode($editable, img[0]);
      }

      ,ENTER: function (e, editor, layoutInfo) {
       
        if (layoutInfo.popover().find('.hint-group').css('display') !== 'block') {
          // apply default enter key
          layoutInfo.holder().summernote('insertParagraph');
        }

        // prevent ENTER key
        return true;
      }

      
    }
    
  });
}));
