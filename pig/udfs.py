@outputSchema("body:map[]")
def parse_body(body_string):
    lines = str(body_string).split(" ")
    body_map = {}
    for line in lines:
        try:
            key, value = line.split("=")
            print key + "\t" + value
            body_map[key] = value
        except:
            pass
    return body_map
