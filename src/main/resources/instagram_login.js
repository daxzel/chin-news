var system = require('system');
console.log('Slimerjs: String instagram login script');
var page = require('webpage').create();

var args = system.args;

var url = 'https://api.instagram.com/oauth/authorize/?client_id='
    + args[1]
    + '&redirect_uri=http://'
    + args[2]
    + ":"
    + args[3]
    + '&response_type=code';

console.log('Slimerjs: Running query: ' + url);
page.open(url, function (status) {
    console.log('Slimerjs: Response status ' + status);
    if (status !== 'success') {
        console.log('Unable to access network');
    } else {
        page.render('/root/test/screenshot.png');
        console.log('Slimerjs: Clicking to button');
        page.evaluate(function (args) {
            document.getElementById("id_username").value = args[4];
            document.getElementById("id_password").value = args[5];
        }, args);
        page.evaluate(function () {
            $("input[type=submit]").click()
        });
    }
    console.log('Slimerjs: closing');
    setInterval(function () {
        console.log('Slimerjs: closing in progress');
        visible = page.evaluate(function () {
            return $("input[type=submit]:visible").length > 0;
        });
        if (!visible) {
            page.close();
            phantom.exit();
        }
        console.log('Slimerjs: closed');
    }, 10000);
});