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
        page.render('/root/test/screenshot2.png')
    }
    console.log('Slimerjs: first click');
    setInterval(function () {
        page.render('/root/test/screenshot4.png');

        console.log('Slimerjs: second window');
        page.includeJs("http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js", function () {
            page.evaluate(function () {
                $("input[value=Authorize]").click()
            });
        });
        setInterval(function () {
            page.render('/root/test/screenshot5.png');
            console.log('Slimerjs: closing in progress');
            page.close();
            phantom.exit();
            console.log('Slimerjs: closed');
        }, 5000);

    }, 5000);
    page.render('/root/test/screenshot3.png');
});