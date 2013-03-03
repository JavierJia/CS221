require 'rest_client'

prefix = 'https://www.googleapis.com/customsearch/v1?key=AIzaSyAro7nL7uI5ShTXZ4MKJNruNMt8I67EXv4&cx=016661682708046640224:5t0ry0getxe' 
site = '&siteSearch=ics.uci.edu'
queries = [ "mondego", 
    "machine%20learning",
    "software%20engineering",
    "security",
    "student%20affairs",
    "graduate%20courses",
    "Crista%20Lopes",
    "REST",
    "computer%20games",
    "information%20retrieval"]

queries.each { |q|
    request= prefix + site + '&q=' + q
    puts request
    response = RestClient.get(request) {|response, request, result,&block|
        case response.code
        when 200
            puts request , ' works'
            File.open( 'data/' + q + '.body', 'w') do |file| 
                file.puts response.to_s
            end
        else
            puts response.code, request
        end
    }
}

