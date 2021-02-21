$(function () {

    /**
     * 初始化时间类型搜索
     */
    $(".js-log-group").on("click", function (e) {
        $(".js-log-self").removeClass("active");
        if ($("#timeQueryForm").is(":visible")) {
            $("#timeQueryForm").hide();
        }

        e.preventDefault();
        $(".js-log-group").removeClass("active");
        $(e.target).addClass("active");
    });

    $(".js-log-self").on("click", function (e) {
        e.preventDefault();
        $("#timeQueryForm").show();

        $(".js-log-group").removeClass("active");
        $(e.target).addClass("active");
    });

    /**
     * 组件时间初始化
     */
    function initDateComponent() {

        // 初始化日期控件
        $("#startTime").datetimepicker({
            language: "zh-CN",
            autoclose: true,
            todayBtn: true,
            format: "yyyy-mm-dd hh:ii:ss",
            startView: 2,
            minView: 0
        }).on('changeDate', function (selected) {
            const minDate = new Date(selected.date.valueOf());
            $('#endTime').datetimepicker('setStartDate', minDate);
        });

        $("#endTime").datetimepicker({
            language: "zh-CN",
            autoclose: true,
            todayBtn: true,
            format: "yyyy-mm-dd hh:ii:ss",
            startView: 2,
            minView: 0,
            initialDate: new Date()
        }).on('changeDate', function (selected) {
            const maxDate = new Date(selected.date.valueOf());
            $('#startTime').datetimepicker('setEndDate', maxDate);
        });
    }

    // 时间控件
    initDateComponent();

    const loadingHtml =
        "<div class='col-xs-12 text-center'>"
        + "<img src='/static/images/loading.gif'/> 数据加载中..."
        + "</div>";

    function searchLogs() {
        $("#log-search-alert").hide();

        const condition = $("#condition").val();
        if (condition === '') {
            $("#log-error-container").text("日志查询条件必填");
            $("#log-search-alert").show();
            return false;
        }

        let count = 0;
        for (let i = 0; i < condition.length; i++) {
            if (condition[i] === "\"")
                count ++;
        }

        if(count %2 !== 0){
            $("#log-error-container").text("引号个数不对");
            $("#log-search-alert").show();
            return false;
        }

        let dateType = undefined;
        const jsLogGroup = $(".js-log-group.active");
        if(jsLogGroup.size() > 0){
            dateType = jsLogGroup.attr("data-type");
        }

        const startTime = $("#startTime").val();
        const endTime = $("#endTime").val();

        const jsLogSelf = $(".js-log-self.active");
        if(jsLogSelf.size() > 0){
            if(startTime === '' || endTime === ''){
                $("#log-error-container").text("开始时间和结束时间必填");
                $("#log-search-alert").show();
                return false;
            }

            const start = Date.parse(startTime);
            const end = Date.parse(endTime);

            if(end - start > 30 * 24 * 3600 * 1000){
                $("#log-error-container").text("开始时间和结束时间只能在1个月之内");
                $("#log-search-alert").show();
                return false;
            }
        }

        const inputData = {
            "dateType": dateType,
            "startTime": startTime,
            "endTime": endTime,
            "condition": condition
        }

        $("#log-results").html(loadingHtml);
        $.ajax(
            "/log/search.html",
            {
                type: 'GET',
                async: false,
                data: inputData,
                success: data => {
                    $("#log-results").html(data);
                    draw();
                }
            }
        );
    }

    function draw(){
        const data = [{
            year: '1951 年',
            sales: 38
        }, {
            year: '1952 年',
            sales: 52
        }, {
            year: '1956 年',
            sales: 61
        }, {
            year: '1957 年',
            sales: 145
        }, {
            year: '1958 年',
            sales: 48
        }, {
            year: '1959 年',
            sales: 38
        }, {
            year: '1960 年',
            sales: 38
        }, {
            year: '1962 年',
            sales: 38
        }];
        const chart = new G2.Chart({
            container: 'mountNode',
            forceFit: true,
            width: 1000,
            height: 300
        });
        chart.source(data);
        chart.scale('sales', {
            tickInterval: 20
        });
        chart.interval().position('year*sales');
        chart.render();
    }

    // 查询日志
    $(".js-log-search").on("click", function () {
        searchLogs();
    });

    // 错误信息隐藏
    $(".js-close-log-alert").on("click", function () {
        $("#log-search-alert").hide();
    });
});