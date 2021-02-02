import './dependencyGraph';

$(function () {
    let services = {};
    let dependencies = {};

    function getDependency() {
        let url = `/dependencies`;
        $.ajax(url, {
            type: 'GET',
            dataType: 'json',
            success: data => {
                const links = data.data;
                buildServiceData(links);
                dependencyDataReceived(links);
            }
        });
    };

    function buildServiceData(links) {
        services = {};
        dependencies = {};
        links.forEach(link => {
            const {parent, child} = link;

            dependencies[parent] = dependencies[parent] || {};
            dependencies[parent][child] = link;

            services[parent] = services[parent] || {serviceName: parent, uses: [], usedBy: []};
            services[child] = services[child] || {serviceName: child, uses: [], usedBy: []};

            services[parent].uses.push(child);
            services[child].usedBy.push(parent);
        });
    };

    // 依赖分析
    $(".js-dependency-analyze").on("click", function () {
        getDependency();
    });
});