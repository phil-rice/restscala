function parse(t) {
    return JSON.parse(t);
}

function getL(lensName, t) {
    var lens = eval(lensName)();
    return lens.get(t)
}

function setL(lensName, t, v) {
    return eval(lensName)().set(t, v)
}

function makeArray() {
    var result = [];
    for (var i = 0; i < arguments.length; i++) {
        result.push(arguments[i])
    }
    return result
}

function shallowCopy(t) {
    var result = {};
    for (var key in t) {
        result[key] = t[key];
    }
    return result;
}

function lens(field) {
    return {
        "get": function (t) {
            return t[field];
        },
        "set": function (t, v) {
            var copy = shallowCopy(t);
            copy[field] = v;
            return copy
        }
    };
}

function lensForLastItemInList() {
    return {
        "get": function (list) {
            return list[list.length-1];
        },
        "set": function (list, item) {
            var newArray = list.slice();
            newArray[list.length-1] = item;
            return newArray
        }
    }
}
function lensForItemInList(n) {
    return {
        "get": function (list) {
            return list[n];
        },
        "set": function (list, item) {
            var newArray = list.slice();
            newArray[n] = item;
            return newArray
        }
    }
}

function compose(l1, l2) {
    return {
        "get": function (t) {
            return l2.get(l1.get(t));
        },
        "set": function (t, v) {
            return l1.set(t, l2.set(l1.get(t), v));
        }
    }
}


function render_json(t) {
    return JSON.stringify(t)
};

function render_pretty(t) {
    return JSON.stringify(t, null, 2)
};

function render_form(t) {
    var name = lens_person_name_string().get(t);
    var line1 = lens_person_line1_string().get(t);
    var line2 = lens_person_line2_string().get(t);
    var html = "<form method='post' action='/person/" + name + "/edit' enctype='application/x-www-form-urlencoded'>" +
        "   <table>" +
        "      <tr><td>Name</td><td><input name ='name'type='text' value='" + name + "' readonly /></td></tr>" +
        "      <tr><td>Line1</td><td><input name='line1' type='text' value='" + line1 + "' /></td></tr>" +
        "      <tr><td>Line2</td><td><input name='line2' type='text' value='" + line2 + "' /></td></tr>" +
        "   </table>" +
        "   <input type='submit'/>" +
        "</form>";
    return html
}

function lens_root() {
    return lens("_embedded");
}
