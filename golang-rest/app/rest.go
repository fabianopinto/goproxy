package app

import (
	"encoding/json"
	"fmt"
	"github.com/gofiber/fiber/v2"
	"log"
	"strconv"
	"time"
)

func ListenAndServe(port string) error {
	app := fiber.New()
	app.Route("/contact", func(api fiber.Router) {
		api.Post("", create)
		api.Get("/:group", readMany)
		api.Route("/:group/:id<int>", func(api fiber.Router) {
			api.Get("", readOne)
			api.Put("", update)
			api.Delete("", de1ete)
		})
	})
	return app.Listen(":" + port)
}

var service = NewContactService()

func create(c *fiber.Ctx) error {
	var contact Contact
	err := json.Unmarshal(c.Body(), &contact)
	if err != nil {
		log.Println(err)
		return c.SendStatus(fiber.StatusBadRequest)
	}
	if err := service.Create(&contact); err != nil {
		log.Println(err)
		return c.SendStatus(fiber.StatusBadRequest)
	}
	c.Set(fiber.HeaderLocation, fmt.Sprintf("%s/contact/%s/%d", c.BaseURL(), contact.Group, contact.Id))
	return c.SendStatus(fiber.StatusCreated)
}
func readMany(c *fiber.Ctx) error {
	lastName := c.Query("lastName")
	versionString := c.Query("version")
	var contacts []Contact
	var err error
	if lastName != "" {
		contacts, err = service.ReadByGroupAndLastName(c.Params("group"), lastName)
	} else if versionString != "" {
		version, err := time.Parse(time.RFC3339, versionString)
		if err != nil {
			return c.SendStatus(fiber.StatusBadRequest)
		}
		contacts, err = service.ReadByGroupAndVersion(c.Params("group"), version)
	} else {
		contacts, err = service.ReadByGroup(c.Params("group"))
	}
	if err != nil {
		return c.SendStatus(fiber.StatusInternalServerError)
	}
	return c.JSON(contacts)
}
func readOne(c *fiber.Ctx) error {
	id, _ := strconv.Atoi(c.Params("id"))
	contacts, err := service.ReadByGroupAndId(c.Params("group"), id)
	if err != nil {
		return c.SendStatus(fiber.StatusNotFound)
	}
	return c.JSON(contacts)
}
func update(c *fiber.Ctx) error {
	id, _ := strconv.Atoi(c.Params("id"))
	var update map[string]any
	if err := json.Unmarshal(c.Body(), &update); err != nil {
		return c.SendStatus(fiber.StatusBadRequest)
	}
	err := service.Update(c.Params("group"), id, update)
	if err != nil && err.Error() == "sql: no rows in result set" {
		return c.SendStatus(fiber.StatusBadRequest)
	}
	return c.SendStatus(fiber.StatusAccepted)
}
func de1ete(c *fiber.Ctx) error {
	id, _ := strconv.Atoi(c.Params("id"))
	if err := service.Delete(c.Params("group"), id); err != nil {
		return c.SendStatus(fiber.StatusInternalServerError)
	}
	return c.SendStatus(fiber.StatusNoContent)
}
