package serializer

import (
	"google.golang.org/protobuf/encoding/protojson"
	"google.golang.org/protobuf/proto"
)

// ProtobufToJSON converts protocol buffer message to JSON string
func ProtobufToJSON(message proto.Message) string {
	opts := protojson.MarshalOptions{
		Indent:          "  ",  // Indent JSON output with two spaces
		EmitUnpopulated: true,  // Include default values for fields
		UseEnumNumbers:  false, // Use names instead of numbers for enum values
		UseProtoNames:   true,  // Use protobuf field names as JSON keys
	}
	return opts.Format(message)
}

// JSONToProtobufMessage converts JSON string to protocol buffer message
func JSONToProtobufMessage(data string, message proto.Message) error {
	return protojson.Unmarshal([]byte(data), message)
}
