package app

import "time"

type ContactService interface {
	Create(*Contact) error
	ReadByGroupAndId(string, int) (*Contact, error)
	ReadByGroup(string) ([]Contact, error)
	ReadByGroupAndLastName(string, string) ([]Contact, error)
	ReadByGroupAndVersion(string, time.Time) ([]Contact, error)
	Update(string, int, map[string]any) error
	Delete(string, int) error
}

func NewContactService() ContactService {
	return &contactService{NewContactRepository()}
}

type contactService struct {
	repository ContactRepository
}

func (s *contactService) Create(contact *Contact) error {
	return s.repository.Save(contact)
}
func (s *contactService) ReadByGroupAndId(group string, id int) (*Contact, error) {
	return s.repository.FindByGroupAndId(group, id)
}
func (s *contactService) ReadByGroup(group string) ([]Contact, error) {
	return s.repository.FindByGroup(group)
}
func (s *contactService) ReadByGroupAndLastName(group, lastName string) ([]Contact, error) {
	return s.repository.FindByGroupAndLastName(group, lastName)
}
func (s *contactService) ReadByGroupAndVersion(group string, version time.Time) ([]Contact, error) {
	return s.repository.FindByGroupAndVersion(group, version)
}
func (s *contactService) Update(group string, id int, update map[string]any) error {
	contact, err := s.repository.FindByGroupAndId(group, id)
	if err != nil {
		return err
	}
	if value, ok := update["firstName"]; ok {
		contact.FirstName = value.(string)
	}
	if value, ok := update["lastName"]; ok {
		contact.LastName = value.(string)
	}
	if value, ok := update["emailAddress"]; ok {
		contact.EmailAddress = value.(string)
	}
	if value, ok := update["phoneNumber"]; ok {
		contact.PhoneNumber = value.(string)
	}
	if value, ok := update["personalNotes"]; ok {
		contact.PersonalNotes = value.(string)
	}
	return s.repository.Update(contact)
}
func (s *contactService) Delete(group string, id int) error {
	return s.repository.DeleteByGroupAndId(group, id)
}
