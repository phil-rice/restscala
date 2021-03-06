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

function lensForFirstItemInList() {
    return {
        "get": function (list) {
            return list[0];
        },
        "set": function (list, item) {
            var newArray = list.slice();
            newArray[0] = item;
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

function lens_root() {
    return lens("_embedded");
}



function lens_person_telephonenumber_telephonenumber(){ return lens("telephoneNumber");}; 
function lens_person_line2_string(){ return lens("line2");}; 
function lens_person_name_string(){ return lens("name");}; 
function lens_telephonenumber_number_string(){ return lens("number");}; 
function lens_person_line1_string(){ return lens("line1");}; 
