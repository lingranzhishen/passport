var bus = new Vue()

function emitError(message) {
    bus.$emit('error', message)
}

function onError(fn) {
    bus.$on('error', fn)
}

function emitInfo(message) {
    bus.$emit('info', message)
}

function onInfo(fn) {
    bus.$on('info', fn)
}

function emitLogin() {
    bus.$emit('login')
}

function onLogin(fn) {
    bus.$on('login', fn)
}

function handleError(errorMessage) {
    emitError(errorMessage)
}

var Alert = {
    template: '#alert',
    data: function () {
        return {
            classObject: {
                'is-danger': false,
                'is-info': false
            },
            message: '',
            visible: false
        }
    },
    mounted: function () {
        onError(function (errorMessage) {
            this.classObject['is-danger'] = true;
            this.message = errorMessage
            this.visible = true
            setTimeout(this.reset.bind(this), 2000)
        }.bind(this))

        onInfo(function (message) {
            this.classObject['is-info'] = true;
            this.message = message
            this.visible = true
            setTimeout(this.reset.bind(this), 2000)
        }.bind(this))
    },
    methods: {
        reset: function () {
            this.visible = false
            this.classObject = {}
        }
    }
}

var Header = {
    template: '#header',
    data: function () {
        return {
            currentUser: JSON.parse(sessionStorage.getItem('currentUser'))
        }
    },
    mounted: function () {
        onLogin(function () {
            this.currentUser = JSON.parse(sessionStorage.getItem('currentUser'));
        }.bind(this))
    },
    methods: {
        logout: function () {
            this.currentUser = null
            sessionStorage.removeItem('token')
            sessionStorage.removeItem('currentUser')
            this.$router.push('/')
        }
    }
}

var taskList = {
    template: '#taskList',
    data: function () {
        return {
            taskList: []
        }
    },
    filters: {
        longToDate: function (long) {
            var date = new Date(long)
            return date.toLocaleString()
        }
    },
    mounted: function () {
        axios.post('/api/identity/list',{page:1},{headers: {token: sessionStorage.getItem('token')}}).then(function (res) {
            if (res.data.res_code!='0000') {
                handleError(res.data.error)
            } else {
                var taskList = res.data.data.data
                this.taskList = taskList.sort(function (a, b) {
                    return b.createdAt - a.createdAt
                })
            }
        }.bind(this))
    },
    methods: {
        showDetail: {}
    }
}

var LoginForm = {
    template: '#login-form',
    data: function () {
        return {
            name: '',
            password: ''
        }
    },
    methods: {
        handleSubmit: function (e) {
            e.preventDefault()
            if (this.name === '') {
                handleError('用户名不能为空')
                return false
            }
            if (this.password === '') {
                handleError('密码不能为空')
                return false
            }
            axios.post('/api/authentication', {
               username:this.name,password:this.password
            }).then(function (res) {
                if (res.data.res_code!="0000") {
                    handleError(res.data.res_msg)
                } else {
                    sessionStorage.setItem('token', res.data.data.token)
                    sessionStorage.setItem('currentUser', JSON.stringify(res.data.data.user));
                    emitLogin()
                    this.$router.push('/taskList')
                }
            }.bind(this))
        }
    }
}

var SignupForm = {
    template: '#signup-form',
    data: function () {
        return {
            name: '',
            password: '',
            passwordAgain: ''
        }
    },
    methods: {
        handleSubmit: function (e) {
            e.preventDefault()
            if (this.name === '') {
                handleError('用户名不能为空')
                return false
            }
            if (this.password === '') {
               handleError('密码不能为空')
                return false
            }
            if (this.password !== this.passwordAgain) {
                handleError('两次输入的密码不一致')
                return false
            }
            axios.post('/api/user', {
                username: this.name,
                password: this.password
            }).then(function (res) {
                if (res.data.error) {
                    handleError(res.data.error)
                } else {
                    emitInfo('注册成功，请登录')
                    this.$router.push('/login')
                }
            }.bind(this))
        }
    }
}


marked.setOptions({
    renderer: new marked.Renderer(),
    gfm: true,
    tables: true,
    breaks: true,
    pedantic: false,
    sanitize: true,
    smartLists: true,
    smartypants: true
});

var taskDetail = {
    template: '#taskDetail',
    data: function () {
        return {
            task: {
            }
        }
    },
    mounted: function () {
        axios.post('/api/identity/' + this.$route.params.id,{},
        {headers: {token: sessionStorage.getItem('token')}}).then(function (res) {
            if (res.data.res_code!="0000") {
                handleError(res.data.res_msg)
                return
            } else {
                this.task = res.data.data
            }
        }.bind(this))
    },
    methods:{
    approve:function (status) {
    var memo = document.getElementById("memo").value;
    axios.post('/api/identity/approve',
                    {
                       userId:this.task.userId,result:status,memo:memo
                    },
                    {headers: {token: sessionStorage.getItem('token')}}).
                    then(function (res) {
                        if (res.data.res_code!="0000") {
                            handleError(res.data.res_msg)
                        } else {
                            emitInfo("操作成功！");
                           router.go(0)
                        }
                    }.bind(this))
    }
    }
}


var NewPost = {
    template: '#new-post',
    data: function () {
        return {
            title: null,
            content: null
        }

    },
    beforeRouteEnter: function (to, from, next) {
        if (sessionStorage.getItem('token') === null) {
            next({path: 'login'})
            emitInfo('请先登录')
        } else {
            next()
        }
    },
    methods: {
        handleSubmit: function (e) {
            if (this.title === null || this.title === '') {
                handleError('标题不能为空')
                return false
            }
            if (this.content === null || this.content === '') {
                handleError('内容不能为空')
                return false
            }
            axios.post('/api/post', {
                    title: this.title,
                    content: this.content,
                }, {
                    headers: {token: sessionStorage.getItem('token')}
                }
            ).then(function (res) {
                if (res.data.error) {
                    handleError(res.data.error)
                    return
                }
                this.$router.push('/posts/' + res.data.id)
            }.bind(this))
        }
    }
}

var routes = [
    {path: '/', component: LoginForm},
    {path: '/taskList', component: taskList},
    {path: '/login', component: LoginForm},
    {path: '/signup', component: SignupForm},
    {path: '/task/detail/:id', component: taskDetail},
]

var router = new VueRouter({
    routes: routes
})

new Vue({
    router: router,
    components: {
        'x-header': Header,
        'x-alert': Alert,
    }
}).$mount('#app')