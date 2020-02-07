# The Description of ClientProtocol

## Introduction

This document describes the message format between the Master and the InsideNode installed in Minecraft.

Currently, Master and InsideNode use Json, Websocket and Netty to communicate. Everything associated to 
the underlying network operation is in the package `io.xsun.minecraft.chatsync.common.communication`.

## Document Format

The description of a message type has several parts:

### `Message Type Name`

- Side: Master, InsideNode or All, which is the sender's side
- Description
- Properties: Json elements

|Name|Type|Description|
|----|----|-----------|
|example1|int|example1|
|example2|string|example2|

- Example:
```json
{
  "example1": 233,
  "example2": "lalala"
}
```

## Common Properties

