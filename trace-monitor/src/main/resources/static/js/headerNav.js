function loadHtml(path) {
    $("#contentHtml").load(path);
}

// 绑定tag点击事件
$(".js-click-tab").on("click", function (e) {
    e.preventDefault();
    e.stopPropagation();

    $("#index-tab-id").find("li").removeClass("active");
    $(e.target).parent().addClass("active");

    const path = $(e.target).attr("data-url");
    loadHtml(path);
});