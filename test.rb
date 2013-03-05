
require "./lib/LOC"
require "webrick"

l = Lines.new

server = WEBrick::HTTPServer.new :Port => 8000
server.mount_proc '/hello' do |req, res|
  res.body = 'Hello, world!'
end
trap 'INT' do server.shutdown end
server.start

