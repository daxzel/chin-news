var system = require('system');
var page = require('webpage').create();

var args = system.args;

var url = 'https://api.instagram.com/oauth/authorize/?client_id='
    + args[0]
    + '&redirect_uri=http://'
    + args[1]
    + ":"
    + args[2]
    + '&response_type=code';

page.open(url, function (status) {
    if (status !== 'success') {
        console.log('Unable to access network');
    } else {
        page.evaluate(function (args) {
            document.getElementById("id_username").value = args[3];
            document.getElementById("id_password").value = args[4];
        }, args);

        var submit = page.evaluate(function () {
            var a =  document.querySelectorAll('input[type=submit]')[0];
            var e = document.createEvent('MouseEvents');
            e.initMouseEvent('click', true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
            a.dispatchEvent(e);
            waitforload = true;
            a.click()
            return a
        });
        //
        //console.log(submit);
        //
        //page.evaluate(function () {
        //
        //});
        page.sendEvent('click', submit.offsetLeft, submit.offsetTop);

        //page.evaluate(function () {
        //
        //});
        //page.render('/Users/tsarevskiy/googleScreenShot.png');

    }
    phantom.exit();
});