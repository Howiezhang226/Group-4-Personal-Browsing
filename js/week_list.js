//atepicker
$(function(){
  $('.datepicker').datepicker({
    "format": 'mm-dd-yyyy',
    "autoclose": true
  });
});

//Import Data
d3.json("json/recipient.json", function(error, result) {
    renderRecipList(result);
})

d3.json("json/js_week.json", function(error, result) {
    data = result;
    var filteredData = data.filter(function(d) {
        return d.day.length > 0;
    });
        
    filteredData.sort(function(a, b) { return d3.ascending(a.week_number, b.week_number)});
        
    renderWeekList(filteredData);
})

d3.json("json/jeff_2001_10.json", function(error, result) {
    //renderWeekList(result);
    renderChart1(result);
    renderChart2(result);
})

d3.json("json/keyword.json", function(error, result) {
    renderChart3(result);
})

// Render functions  
function renderChart1(data) {
    //Left side: Week List
    var chartWidth = 700;
    var chartHeight = 350;
    var charMargin = {top: 40, left: 40, right: 40, bottom: 40};
    var chartInnerWidth = chartWidth - charMargin.left - charMargin.right;
    var chartInnerHeight = chartHeight - charMargin.top - charMargin.bottom;
    var chart1 = d3.select("#chart1");

    chart1.attr("width", chartWidth)
         .attr("height", chartHeight)
         .attr("transform", "translate(" + charMargin.left + "," + charMargin.top + ")");
    
}

function renderChart2(data) {
    var chartWidth = 350;
    var chartHeight = 200;
    var charMargin = {top: 0, left: 40, right: 40, bottom: 20};
    var chartInnerWidth = chartWidth - charMargin.left - charMargin.right;
    var chartInnerHeight = chartHeight - charMargin.top - charMargin.bottom;
    var chart2 = d3.select("#chart2");

    chart2.attr("width", chartWidth)
         .attr("height", chartHeight)
         .attr("transform", "translate(" + charMargin.left + "," + charMargin.top + ")");;

    var xScale = d3.scale.linear()
        .range([0, chartInnerWidth])
        .domain([0, d3.max(data, function(d) { return d.num_recipent; })]);

    var yScale = d3.scale.ordinal()
        .rangeRoundBands([chartInnerHeight, 0], .2)
        .domain(data.map(function(d) { return d.day; }));

    var xAxis = d3.svg.axis()
        .scale(xScale)
        .orient("bottom");

    var yAxis = d3.svg.axis()
        .scale(yScale)
        .orient("left");

    chart2.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + charMargin.left + ", " + (chartInnerHeight + charMargin.top) + ")")
        .call(xAxis);

    chart2.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + charMargin.left + ", " + charMargin.top + ")")
        .call(yAxis);

    chart2.append("text")
        .text("Recipient")
        .style({"font-size": "15px", fill: "#ccc"})
        .attr("dx", 250)
        .attr("dy", 150);

    chart2.selectAll(".bar")
      .data(data)
      .enter()
      .append("rect")
      .attr("class", "bar")
      //.attr("x", charMargin.left)
      .attr("y", function(d) { return yScale(d.day); })
      .attr("width", function(d) { return xScale(d.num_recipent); })
      .attr("height", yScale.rangeBand())
      .attr("transform", "translate(" + charMargin.left + ", " + charMargin.top + ")")
      .attr("fill", "#5bc0de");   
}

function renderChart3(data) {
    var chartWidth = 350;
    var chartHeight = 200;
    var charMargin = {top: 0, left: 60, right: 40, bottom: 20};
    var chartInnerWidth = chartWidth - charMargin.left - charMargin.right;
    var chartInnerHeight = chartHeight - charMargin.top - charMargin.bottom;
    var chart3 = d3.select("#chart3");

    chart3.attr("width", chartWidth)
         .attr("height", chartHeight)
         .attr("transform", "translate(" + charMargin.left + "," + charMargin.top + ")");;

    var xScale = d3.scale.linear()
        .range([0, chartInnerWidth])
        .domain([0, d3.max(data, function(d) { return d.frequency; })]);

    var yScale = d3.scale.ordinal()
        .rangeRoundBands([chartInnerHeight, 0], .2)
        .domain(data.map(function(d) { return d.keyword; }));

    var xAxis = d3.svg.axis()
        .scale(xScale)
        .orient("bottom");

    var yAxis = d3.svg.axis()
        .scale(yScale)
        .orient("left");

    chart3.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + charMargin.left + ", " + (chartInnerHeight + charMargin.top) + ")")
        .call(xAxis);

    chart3.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + charMargin.left + ", " + charMargin.top + ")")
        .call(yAxis);

    chart3.append("text")
        .text("Keyword")
        .style({"font-size": "15px", fill: "#ccc"})
        .attr("dx", 250)
        .attr("dy", 150);

    chart3.selectAll(".bar")
      .data(data)
      .enter()
      .append("rect")
      .attr("class", "bar")
      //.attr("x", charMargin.left)
      .attr("y", function(d) { return yScale(d.keyword); })
      .attr("width", function(d) { return xScale(d.frequency); })
      .attr("height", yScale.rangeBand())
      .attr("transform", "translate(" + charMargin.left + ", " + charMargin.top + ")")
      .attr("fill", "#5cb85c");  
}

function renderWeekList(data) {
    //Left side: Week List
    var chartWidth = 160;
    var chartHeight = 100;
    var charMargin = {top: 10, left: 20, right: 10, bottom: 20};
    var chartInnerWidth = chartWidth - charMargin.left - charMargin.right;
    var chartInnerHeight = chartHeight - charMargin.top - charMargin.bottom;

    var xScale = d3.scale.ordinal()
        .rangeRoundBands([0, chartInnerWidth], .1)
        .domain(["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"]);

    var yScale = d3.scale.linear()
        .range([chartInnerHeight, 0])
        .domain([0, 10, 30]);

    var xAxis = d3.svg.axis()
        .scale(xScale)
        .orient("bottom");

    var yAxis = d3.svg.axis()
        .scale(yScale)
        .orient("left");

    var weekList = d3.select("#weekList");

    var weekLi = weekList.selectAll("a")
        .data(data)
        .enter().append("a")
        .attr("class", "list-group-item")
        .attr("href", "#")
        .on("click", function(d, i) {
            //alert(d.week_number);
            d3.select("#timeFrame").html(d.start_date + " - " + d.end_date);
        });

    var weekSvg = weekLi.append("svg")
        .attr("id", function(d, i){return "week" + i})
        .attr("width", chartWidth)
        .attr("height", chartHeight)
        .attr("transform", "translate(" + charMargin.left + "," + charMargin.top + ")");

    weekSvg.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + charMargin.left + ", " + (chartInnerHeight + charMargin.top) + ")")
        .call(xAxis);

    weekSvg.append("g")
        .attr("class", "axis")
        .attr("transform", "translate(" + charMargin.left + ", " + charMargin.top + ")")
        .call(yAxis);
    
    for (var j = 0; j < data.length; j++) {
        var iweek = d3.select("#week" + j);
        var weekData = data[j];
        iweek.append("text")
            .text(weekData.start_date + " - " + weekData.end_date)
            .style({"font-size": "12px", fill: "#ccc"})
            .attr("dx", 30)
            .attr("dy", 10);

        iweek.selectAll(".bar")
          .data(weekData.day)
          .enter()
          .append("rect")
          .attr("class", "bar")
          .attr("x", function(d) { return xScale(d.day); })
          .attr("y", function(d) { return yScale(d.day_num); })
          .attr("width", xScale.rangeBand())
          .attr("height", function(d) { return chartInnerHeight - yScale(d.day_num); })
          .attr("transform", "translate(" + charMargin.left + ", " + charMargin.top + ")")
          .attr("fill", "#337ab7");  
    }    
    
}

function renderRecipList(data) {
    //Right side: Recipient list
    d3.select("#recipList").selectAll('tr')
        .data(data)
        .enter()
        .append('tr')
        .attr("class", "text-primary")
        .text(function(d) {return d.name});
}








