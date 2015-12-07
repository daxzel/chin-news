console.log('Check logging slimer');

var system = require('system');
var page = require('webpage').create();

var args = system.args;

var url = 'https://api.instagram.com/oauth/authorize/?client_id='
    + args[1]
    + '&redirect_uri=http://'
    + args[2]
    + ":"
    + args[3]
    + '&response_type=code';

page.open(url, function (status) {
    if (status !== 'success') {
        console.log('Unable to access network');
    } else {
        page.evaluate(function (args) {
            document.getElementById("id_username").value = args[4];
            document.getElementById("id_password").value = args[5];
        }, args);
        page.evaluate(function () {
            $("input[type=submit]").click()
        });
    }
    setInterval(function () {
        visible = page.evaluate(function () {
            return $("input[type=submit]:visible").length > 0;
        });
        if (!visible) {
            page.close();
            phantom.exit();
        }
    }, 3000);
});