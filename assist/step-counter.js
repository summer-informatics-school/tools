var http = require('http');
var url = require('url');

var step = 0;
for (var i in process.argv) {
   if (i < 2)
     continue;
   step = parseInt(process.argv[i]);
}

var step_waits = [];
function step_update( cont ) {
    cont({'result': 'ok'});
    var waits = step_waits;
    step_waits = [];
    for (var i in waits) {
        waits[i].update();
    }
}

var actions = {
    'step' : function( args, cont ) {
        cont({'step': step});
    },
    'step:wait' : function( args, cont ) {
        var step_old = args['step'];
        if (step_old != undefined && step_old != step) {
            cont({'step': step});
        }
        var waiter = {
            'cont': cont,
            'update': undefined 
        };
        waiter.update = function() {
            var cont = waiter.cont;
            waiter.cont = undefined;
            clearTimeout(waiter.timer);
            if (cont == undefined) {
                return;
            }
            cont({'step': step});
        }
        waiter.timer = setTimeout(waiter.update, 4000);
        step_waits.push(waiter);
    },
    'next' : function( args, cont ) {
        //  console.log('next');
        step++;
        step_update(cont);
    },
    'prev' : function( args, cont ) {
        // console.log('prev');
        step--;
        step_update(cont);
    },
    'set' : function( args, cont ) {
        step = args['step'];
        step_update(cont);
    },
};

http.createServer(function (req, res) {
    var query = url.parse(req.url, true);
    var path = query.pathname;
    var args = query.query;
    if (path != '/assist-control') {
        res.writeHead(404, {'Content-Type': 'text/plain'});
        res.end('nothing here');
        return;
    }
    // console.log('action=' + args.action);
    action = actions[args.action];
    if (action == undefined) {
        res.writeHead(404, {'Content-Type': 'text/plain'});
        res.end('bad method');
        return;
    }
    action(args, function(json) {
        res.writeHead(200, {'Content-Type': 'application/json'});
        res.end(JSON.stringify(json));
    });
}).listen(8081, '127.0.0.1');

