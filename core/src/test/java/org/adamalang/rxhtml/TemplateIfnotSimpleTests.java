/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.rxhtml;

public class TemplateIfnotSimpleTests extends BaseRxHtmlTest {
  @Override
  public boolean dev() {
    return false;
  }
  @Override
  public String issues() {
    StringBuilder issues = new StringBuilder();
    issues.append("");
    return issues.toString();
  }
  @Override
  public String gold() {
    StringBuilder gold = new StringBuilder();
    gold.append("JavaScript:(function($){");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/\">");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <div rx:ifnot=\"obj\">");
    gold.append("\n    var d=$.E('div');");
    gold.append("\n    $.IF(d,a,a,'obj',false,false,function(f,e) {");
    gold.append("\n      f.append($.L(e,'key'));");
    gold.append("\n      f.append($.T(' - '));");
    gold.append("\n      f.append($.L(e,'value'));");
    gold.append("\n    },function(f,e) {");
    gold.append("\n");
    gold.append("\n      // <div rx:else=\"\">");
    gold.append("\n      var g=$.E('div');");
    gold.append("\n      g.append($.T(' Not set! '));");
    gold.append("\n      f.append(g);");
    gold.append("\n    },false);");
    gold.append("\n    b.append(d);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"/libadama.js/GENMODE.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n");
    gold.append("\n  // <page uri=\"/\">");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    var c=$.X();");
    gold.append("\n");
    gold.append("\n    // <div rx:ifnot=\"obj\">");
    gold.append("\n    var d=$.E('div');");
    gold.append("\n    $.IF(d,a,a,'obj',false,false,function(f,e) {");
    gold.append("\n      f.append($.L(e,'key'));");
    gold.append("\n      f.append($.T(' - '));");
    gold.append("\n      f.append($.L(e,'value'));");
    gold.append("\n    },function(f,e) {");
    gold.append("\n");
    gold.append("\n      // <div rx:else=\"\">");
    gold.append("\n      var g=$.E('div');");
    gold.append("\n      g.append($.T(' Not set! '));");
    gold.append("\n      f.append(g);");
    gold.append("\n    },false);");
    gold.append("\n    b.append(d);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n</script><style>");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n</style></head><body></body><script>");
    gold.append("\n  RxHTML.init();");
    gold.append("\n</script></html>");
    return gold.toString();
  }
  @Override
  public String source() {
    StringBuilder source = new StringBuilder();
    source.append("<forest>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n        <div rx:ifnot=\"obj\">");
    source.append("\n            <lookup path=\"key\" /> - <lookup path=\"value\" />");
    source.append("\n            <div rx:else>");
    source.append("\n                Not set!");
    source.append("\n            </div>");
    source.append("\n        </div>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
  @Override
  public String schema() {
    StringBuilder gold = new StringBuilder();
    gold.append("{");
    gold.append("\n  \"/\" : {");
    gold.append("\n    \"obj\" : \"bool\"");
    gold.append("\n  }");
    gold.append("\n}");
    return gold.toString();
  }
}
