extends Node

signal logged_in
signal logged_out
var _fba = null
var _web = false
var _web_user = null
var _web_profile = null

func _ready():
    if type_exists('FirebaseAuth'):
        _fba = ClassDB.instance('FirebaseAuth')
    elif Engine.has_singleton('FirebaseAuth'):
        _fba = Engine.get_singleton('FirebaseAuth')
    elif OS.has_feature('HTML5'):
        _web = true
        set_process(true)
        JavaScript.eval("""
        var _firebase_auth_user = null;
        firebase.auth().onAuthStateChanged(function(user) {
            if (user) {
                // User is signed in.
                _firebase_auth_user = JSON.stringify(user);
            } else {
                // No user is signed in.
                _firebase_auth_user = null;
            }
        });
        """, true)
    else:
        push_warning('FirebaseAuth module not found!')
    if _fba != null:
        _fba.connect('logged_in', self, '_on_logged_in')

func _process(dt):
    # check web results
    if _web:
        var json = JavaScript.eval("_firebase_auth_user;", true);
        if json != null and _web_user == null:
            var res = JSON.parse(json)
            if res.error == OK:
                var user = res.result
                if user != null:
                    # login
                    _web_user = user
                    if _web_profile != null:
                        for key in _web_profile:
                            _web_user[key] = _web_profile[key]
                    emit_signal('logged_in')
                    print('Logged in as web user: %s'%var2str(_web_user));
        elif json == null and _web_user != null:
            # logout 
            _web_user = null
            emit_signal('logged_out')

func login():
    if _fba != null:
        _fba.sign_in_anonymously()

func login_facebook(token):
    if _fba != null:
        _fba.sign_in_facebook(token)

func login_custom_token(token, user_profile = null):
    if _web:
        if user_profile != null:
            _web_profile = user_profile
        print('Login with web token: %s'%token)
        var js = """
        firebase.auth().signInWithCustomToken('%s').then(function(userInfo) {
            console.log('Authenticated as', userInfo);
            var profile = %s;
            if(profile !== null) {
                userInfo.user.updateProfile(profile);
            }
        }).catch(function(error) {
            console.error('Firebase Auth error:', error);
        });
        """%[token, JSON.print(user_profile)]
        JavaScript.eval(js, true)
        #Log.info('JS: %s'%js)

func is_logged_in():
    if _fba != null:
        return _fba.is_logged_in()
    elif _web:
        return _web_user != null;
    else:
        return false

func user_name():
    if _fba != null and is_logged_in():
        return _fba.user_name()
    elif _web and 'displayName' in _web_user:
        return _web_user.displayName
    else:
        return null

func avatar_url():
    if _fba != null and is_logged_in():
        return _fba.photo_url()
    elif _web and 'photoURL' in _web_user:
        return _web_user.photoURL
    else:
        return null

func email():
    if _fba != null and is_logged_in():
        return _fba.email()
    elif _web and 'email' in _web_user:
        return _web_user.email
    else:
        return null

func uid():
    if _fba != null and is_logged_in():
        return _fba.uid()
    elif _web and 'uid' in _web_user:
        return _web_user.uid
    else:
        return null

func log_out():
    if _fba != null and is_logged_in():
        _fba.sign_out()
        print('[Auth] Logged Out!')
        emit_signal('logged_out')

func _on_logged_in():
    print('[Auth] Logged In!')
    emit_signal('logged_in')
