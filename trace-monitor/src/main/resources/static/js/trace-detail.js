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

    function hideSpan(id){
        const span = $("[id='"+id+"']");
        span.hide();

        if($(span).get(0).hasAttribute("data-child-ids")){
            const childIds = $(span).get(0).getAttribute("data-child-ids");
            childIds.substring(1,childIds.length-1).split(",").forEach(childId => {
                hideSpan(childId);
            });
        }
    }

    function showSpan(id){
        const span = $("[id='"+id+"']");
        span.show();

        const expander = $(span).find(".expander");
        if(expander){
            $(expander).text("-");
        }

        if($(span).get(0).hasAttribute("data-child-ids")){
            const childIds = $(span).get(0).getAttribute("data-child-ids");
            childIds.substring(1,childIds.length-1).split(",").forEach(childId => {
                showSpan(childId);
            });
        }
    }

    $(".expander").on("click", function (e) {
        e.preventDefault();
        const parentSpan = $(e.target).parent().parent().parent().get(0);
        const childIds = parentSpan.getAttribute("data-child-ids");
        const showText = $(e.target).text();

        if (childIds) {
            if (showText === "-") {
                $(e.target).text("+");
                childIds.substring(1,childIds.length-1).split(",").forEach(childId => {
                    hideSpan(childId.trim());
                });
            }
            else {
                $(e.target).text("-");
                childIds.substring(1,childIds.length-1).split(",").forEach(childId => {
                    showSpan(childId.trim());
                });
            }
        }
    });
});