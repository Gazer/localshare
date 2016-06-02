# Run with 'rackup'

require 'rubygems'
require 'bundler'
require 'json'

Bundler.require


if ARGV.empty?
  puts "$> ruby server.app YourIp file1 file2 ..."
  return
elsif ARGV.size == 1
  puts "$> ruby server.app YourIp file1 file2 ..."
  return
end

my_ip = ARGV[0]

set :port, 8080
set :bind, my_ip

files = []
filenames = []
ARGV[1..-1].each_with_index do |filename, idx|
  filenames << filename
  files << {
    "name": File.basename(filename),
    "size": File.size(filename),
    "contentType": `file --mime -b '#{filename}'`.split(/;/).first,
    "url": "http://#{my_ip}:8080/get/#{idx}"
  }
end

get '/sharer' do
  ret = {
    "ssid": "home",
    "files": files
  }

  ret.to_json
end

get '/get/:id' do
  sleep 2 + rand * 5
  id = params[:id].to_i
  file = files[id]
  send_file open(filenames[id]), type: file[:contentType], disposition: 'inline'
end
