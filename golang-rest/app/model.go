package app

import "time"

type Contact struct {
	Id            int
	FirstName     string
	LastName      string
	EmailAddress  string
	PhoneNumber   string
	PersonalNotes string
	LastModified  time.Time
	Group         string
}
