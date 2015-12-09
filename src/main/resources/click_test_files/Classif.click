src :: FromDevice
dst :: ToDevice

cl_incoming :: IPClassifier(dst net 0.0.0.0/8, dst host 0.0.0.0, -)

src -> cl_incoming[0] -> dst
cl_incoming[2] -> dst