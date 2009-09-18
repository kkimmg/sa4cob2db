#!/usr/bin/ruby
if ARGV[1] == nil
  outname = ARGV[0]
  inname = outname + ".bak"
  File.rename(outname, inname)
else
  inname = ARGV[0]
  outname = ARGV[1]
end
infile = open(inname)
outfile = open(outname, "w")
while text = infile.gets do
  rowtxt = text.unpack("a6a*")[1]
  outfile.puts rowtxt
end
outfile.close
infile.close
