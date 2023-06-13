module.exports  = [
    {
        context: ['/**'],
        target: 'http://localhost:8080',
        secure: false,
        headers: {
            "Access-Control-Allow-Origin": "https://checkout.stripe.com", 
            "Access-Control-Allow-Methods" : "GET, POST, PUT, DELETE, OPTIONS",
            "Access-Control-Allow-Headers" : "Origin, X-Requested-With, Content-Type, Accept, Authorization"
        }
    }
]