$(function () {
    function hideSameJvm() {
        const appKeys = $(".app-key-handle");
        const ips = $(".ip-handle");

        let i = appKeys.size() - 1;
        for (; i >= 0; i--) {
            if ($(appKeys[i]).text() === $(appKeys[i - 1]).text() && $(ips[i]).text() === $(ips[i - 1]).text()) {
                $(appKeys[i]).text("-");
                $(ips[i]).text("-");
            }
        }
    }

    hideSameJvm();



});