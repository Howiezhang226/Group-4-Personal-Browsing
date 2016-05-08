//Global Variables
var weekNum = 25;

//Import Data
// d3.json("json/recipient.json", function(error, result) {
//     renderRecipList(result);
// })
var mainChartData = []
d3.json("json/js_week.json", function(error, result) {
    data = result;
    mainChartData = result;
    var filteredData = data.filter(function(d) {
        return d.days.length > 0;
    });
    filteredData.sort(function(a, b) { return d3.ascending(a.week_number, b.week_number)});
        
    renderWeekList(filteredData);
    renderRecipList(filteredData);
    renderMainChart(mainChartData, weekNum);
})

d3.json("json/jeff_2001_10.json", function(error, result) {
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
        .domain([0, 10, 100]);

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
            weekNum = d.week_number;
            d3.select("#timeFrame").html(d.start_date + " - " + d.end_date);
            renderRecipList(data);
            renderMainChart(mainChartData, weekNum);
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
          .data(weekData.days)
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
        .data(data, function(d) {
            // console.log("weekNum: " + weekNum);
            if (d.week_number == weekNum) {
                console.log("d.week_number" + d.week_number);
                console.log("d.sent_detail" + d.sent_detail);
                return d.sent_detail;
            }   
        })
        .enter()
        .append('tr')
        .attr("class", "text-primary")
        .text(function(d) {return d.name});
}




//global variable for main chart. Render only once.

var margin = {top: 30, right: 50, bottom: 20, left: 30},
  width = 700 - margin.left - margin.right,
  height = 350 - margin.top - margin.bottom,
  gridSize = Math.floor(height / 8),
  days = ["Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"],
  times = ["1a", "2a", "3a", "4a", "5a", "6a", "7a", "8a", "9a", "10a", "11a", "12a", "1p", "2p", "3p", "4p", "5p", "6p", "7p", "8p", "9p", "10p", "11p", "12p"],
  gridWeight = gridSize * 2 / 3;
var svg = d3.select("#chart1").append("svg")
  .attr("width", width + margin.left + margin.right)
  .attr("height", height + margin.top + margin.bottom);
var barChart = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")");
var circleChart = svg.append("g").attr("transform", "translate(" + gridWeight * 24.8 + "," + margin.top + ")");
var dayLabels = barChart.selectAll(".dayLabel")
  .data(days)
  .enter().append("text")
  .text(function (d) {
      return d;
  })
  .attr("x", 0)
  .attr("y", function (d, i) {
      return i * gridSize;
  })
  .style("text-anchor", "end")
  .attr("transform", "translate(-6," + gridSize + ")")
  .attr("class", function (d, i) {
      return ((i >= 0 && i <= 4) ? "dayLabel mono axis axis-workweek" : "dayLabel mono axis");
  });

var timeLabels = barChart.selectAll(".timeLabel")
  .data(times)
  .enter().append("text")
  .text(function (d) {
      return d;
  })
  .attr("x", function (d, i) {
      return i * gridWeight;
  })
  .attr("y", 0)
  .style("text-anchor", "middle")
  .attr("transform", "translate(" + gridSize / 3 + ", " + (height) + ")")
  .attr("class", function (d, i) {
      return ((i >= 7 && i <= 16) ? "timeLabel mono axis axis-worktime" : "timeLabel mono axis");
  });

var renderMainChart = function (data, week) {
    var dataFiltered = data.filter(function (d) {
        return d.week_number == week;
    });
    var results = [];
    var circles = [];
    for (var i = 0; i < dataFiltered[0].days.length; i++) {
        var day_data = dataFiltered[0].days;
        //console.log(day_data[i].length);


        var day = day_data[i]["day_num"];
        var circle_result = {};
        circle_result["day"] = day;
        circle_result["total"] = day_data[i]["total"];
        circles.push(circle_result);
        var myMap = day_data[i]["daytime"];
        for (var key in myMap) {
            var result = {};
            result["day"] = day;
            result["hour"] = key;
            result["value"] = myMap[key];
            results.push(result);
        }


    }
    console.log(results.length);
    console.log(circles.length);
    //console.log(circles);
    var rectScale = d3.scale.linear()
      .domain([0, 16])
      .range([0, gridSize]);

    var rScale = d3.scale.linear().range([5, gridSize / 2]).domain([0, 50]);
    svg.selectAll(".hour").remove();
    svg.selectAll("circle").remove();
    var cards = barChart.selectAll(".hour")
      .data(results, function (d) {
          return d.day + ':' + d.hour;
      });

    //cards.append("title");

    cards.enter().append("rect")
      .attr("x", function (d) {
          return (d.hour) * gridWeight;
      })
      .attr("y", function (d) {
          return (d.day) * gridSize + gridSize - rectScale(d.value);
      })
      .attr("rx", 4)
      .attr("ry", 4)
      .attr("class", "hour bordered")
      .attr("width", gridWeight)
      .attr("height", function (d) {
            return rectScale(d.value);
        }
      )
      .style("fill", "#225ea8");
//
//                    cards.transition().duration(1000)
//                            .style("fill", function(d) { return colorScale(d.value); });

    //cards.select("title").text(function(d) { return d.value; });
    console.log(circles);
    var selection = circleChart.selectAll("circle").data(circles, function (d) {
        console.log(d.day + ':' + d.total);
        return d.day + ':' + d.total;
    });

    selection.enter()
      .append("circle")
      .on("click", function (d, i) {
          alert(d.day + ':' + d.total);
      })
      .attr("r", function (d, i) {
          return rScale(d.total);
      })
      .attr("cx", 40)
      .attr("cy", function (d, i) {
          return (1 + parseInt(d.day) ) * gridSize;
      })
      .attr("fill", "#225ea8");

    cards.exit().remove();
    selection.exit().remove();

};

